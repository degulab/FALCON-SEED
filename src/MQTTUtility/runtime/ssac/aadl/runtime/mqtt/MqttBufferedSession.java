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
 * @(#)MqttBufferedSession.java	0.2.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

import java.util.List;

/**
 * サブスクライブしたメッセージをバッファリングする、MQTT セッションのインタフェース。
 * 
 * @version 0.2.0	2013/05/15
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public interface MqttBufferedSession extends MqttSession
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * バッファリングされたメッセージが一つもない場合に <tt>true</tt> を返す。
	 * @return	バッファリングされた受信済みメッセージが存在しない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isMessageEmpty();

	/**
	 * バッファリングされているメッセージ数を返す。
	 * @return	バッファリングされている受信済みメッセージ数
	 */
	public int getMessageCount();

	/**
	 * バッファリングされているメッセージをすべて破棄する。
	 */
	public void clearMessages();

	/**
	 * 指定されたトピックに一致する受信済みメッセージがバッファリングされているかを判定する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilter	判定するトピック（ワイルドカードの指定も可）
	 * @return	トピックに一致するメッセージを受信済みの場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsMessage(String topicFilter);

	/**
	 * <em>topicFilters</em> に含まれるすべてのトピックに一致する受信済みメッセージがバッファリングされているかを判定する。
	 * <em>topicFilters</em> の要素が空の場合、このメソッドは <tt>false</tt> を返す。
	 * @param topicFilters	判定対象のトピック（ワイルドカードの指定も可）のリスト
	 * @return	バッファリングされている受信済みメッセージが <em>topicFilters</em> のすべての要素と一致した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 */
	public boolean containsAllMessages(List<String> topicFilters);
	
	/**
	 * 指定されたトピックに一致する受信済みメッセージ以外を、バッファからすべて削除する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilter	バッファに残すトピック（ワイルドカードの指定も可）
	 * @return	この呼び出しによりバッファの内容が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 */
	public boolean retainMessage(String topicFilter);
	
	/**
	 * <em>topicFilters</em> に含まれるトピックに一致する受信済みメッセージ以外を、バッファからすべて削除する。
	 * <em>topicFilters</em> の要素が空の場合、すべての受信済みメッセージが削除される。
	 * @param topicFilters	バッファに残すトピック（ワイルドカードの指定も可）のリスト
	 * @return	この呼び出しによりバッファの内容が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 */
	public boolean retainAllMessages(List<String> topicFilters);
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * @param remove		取得したメッセージを受信済みメッセージから除去する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage getMessage(boolean remove) throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に負の値を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * @param timeout		待機時間をミリ秒で指定する。負の値を指定した場合はメッセージを受信するまで待機する。
	 * @param remove		取得したメッセージを受信済みメッセージから除去する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage getMessage(long timeout, boolean remove) throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致するトピックを持つ、
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * @param topicFilter	取得対象のトピック（ワイルドカードの指定も可）
	 * @param remove		取得したメッセージを受信済みメッセージから除去する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage getFilteredMessage(String topicFilter, boolean remove) throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致するトピックを持つ、
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に負の値を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * @param topicFilter	取得対象のトピック（ワイルドカードの指定も可）
	 * @param timeout		待機時間をミリ秒で指定する。負の値を指定した場合はメッセージを受信するまで待機する。
	 * @param remove		取得したメッセージを受信済みメッセージから除去する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage getFilteredMessage(String topicFilter, long timeout, boolean remove) throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public void waitMessage() throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージが存在しない場合、メッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に負の値を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * @param timeout	待機時間をミリ秒で指定する。負の値を指定した場合はメッセージを受信するまで待機する。
	 * @return	受信済みメッセージが存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public boolean waitMessage(long timeout) throws InterruptedException;
	
	/**
	 * <em>topicFilter</em> に指定されたトピックと一致するバッファリングされた受信済みメッセージが存在しない場合、
	 * 指定されたトピックに一致するメッセージを受信するまで処理をブロックする。
	 * @param topicFilter		待機対象のトピック（ワイルドカードの指定も可）
	 * @throws NullPointerException	<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public void waitMessage(String topicFilter) throws InterruptedException;
	
	/**
	 * <em>topicFilter</em> に指定されたトピックと一致するバッファリングされた受信済みメッセージが存在しない場合、
	 * 指定されたトピックに一致するメッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に負の値を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * @param topicFilter		待機対象のトピック（ワイルドカードの指定も可）
	 * @param timeout			待機時間をミリ秒で指定する。負の値を指定した場合はメッセージを受信するまで待機する。
	 * @return	トピックに一致するメッセージを受信済みの場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public boolean waitMessage(String topicFilter, long timeout) throws InterruptedException;

	/**
	 * <em>topicFilters</em> に含まれるすべてのトピックに一致するメッセージを受信するまで、処理をブロックする。
	 * <em>topicFilters</em> の要素が空の場合、このメソッドは何らかのメッセージを受信するまで、処理をブロックする。
	 * @param topicFilters		待機対象のトピック（ワイルドカードの指定も可）のリスト
	 * @throws NullPointerException	<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public void waitAllMessages(List<String> topicFilters) throws InterruptedException;
	
	/**
	 * <em>topicFilters</em> に含まれるすべてのトピックに一致するメッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に負の値を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * <em>topicFilters</em> の要素が空の場合、このメソッドは何らかのメッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * @param topicFilters		待機対象のトピック（ワイルドカードの指定も可）のリスト
	 * @param timeout			待機時間をミリ秒で指定する。負の値を指定した場合はメッセージを受信するまで待機する。
	 * @return	トピックに一致するメッセージをすべて受信済みの場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public boolean waitAllMessages(List<String> topicFilters, long timeout) throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、このオブジェクトから削除される。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage popMessage() throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に負の値を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、このオブジェクトから削除される。
	 * @param timeout			待機時間をミリ秒で指定する。負の値を指定した場合はメッセージを受信するまで待機する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws IllegalArgumentException	<em>timeout</em> の値が負である場合 
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage popMessage(long timeout) throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致するもっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param topicFilter		取得対象のトピック（ワイルドカードの指定も可）
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage popFilteredMessage(String topicFilter) throws InterruptedException;
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致するもっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に負の値を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、このバッファから削除される。
	 * @param topicFilter		取得対象のトピック（ワイルドカードの指定も可）
	 * @param timeout			待機時間をミリ秒で指定する。負の値を指定した場合はメッセージを受信するまで待機する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage popFilteredMessage(String topicFilter, long timeout) throws InterruptedException;
}
