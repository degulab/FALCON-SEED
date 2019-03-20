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
 * @(#)MqttSubscriber.java	0.2.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

import java.util.List;

/**
 * MQTT セッションに対し、サブスクライブするインタフェース群。
 * 
 * @version 0.2.0	2013/05/15
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttSubscriber extends MqttUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected MqttSubscriber() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたトピックに対し、メッセージ伝達品質（QoS）に 1 を指定してメッセージのサブスクライブ（購読）を開始する。
	 * サブスクライブの開始によって、指定されたトピックにメッセージが配信されたとき、
	 * ここで指定したセッションがメッセージを受信できるようになる。
	 * トピックにはワイルドカードが指定できる。
	 * @param session		使用するセッション
	 * @param topicFilter	トピック（ワイルドカードの指定も可）
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	static public void subscribe(MqttSession session, String topicFilter) {
		subscribe(session, topicFilter, 1);
	}
	
	/**
	 * 指定されたトピックに対して、メッセージのサブスクライブ（購読）を開始する。
	 * サブスクライブの開始によって、指定されたトピックにメッセージが配信されたとき、
	 * ここで指定したセッションがメッセージを受信できるようになる。
	 * トピックにはワイルドカードが指定できる。
	 * @param session		使用するセッション
	 * @param topicFilter	トピック（ワイルドカードの指定も可）
	 * @param qos	メッセージ伝達品質（0, 1, 2）
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	static public void subscribe(MqttSession session, String topicFilter, int qos) {
		session.subscribe(topicFilter, qos);
	}

	/**
	 * <em>topicFilters</em> に含まれるすべてのトピックに対し、メッセージ伝達品質（QoS）に 1 を指定してメッセージのサブスクライブ（購読）を開始する。
	 * サブスクライブの開始によって、指定されたトピックにメッセージが配信されたとき、
	 * ここで指定したセッションがメッセージを受信できるようになる。
	 * トピックにはワイルドカードが指定できる。
	 * @param session		使用するセッション
	 * @param topicFilters	トピック（ワイルドカードの指定も可）のリスト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	static public void subscribe(MqttSession session, List<String> topicFilters) {
		subscribe(session, topicFilters, 1);
	}
	
	/**
	 * <em>topicFilters</em> に含まれるすべてのトピックに対して、メッセージのサブスクライブ（購読）を開始する。
	 * サブスクライブの開始によって、指定されたトピックにメッセージが配信されたとき、
	 * ここで指定したセッションがメッセージを受信できるようになる。
	 * トピックにはワイルドカードが指定できる。
	 * @param session		使用するセッション
	 * @param topicFilters	トピック（ワイルドカードの指定も可）のリスト
	 * @param qos	メッセージ伝達品質（0, 1, 2）
	 * @throws NullPointerException	<em>session</em>、<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合、
	 * 									もしくは、<em>topicFilters</em> の要素に空文字が含まれている場合
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	static public void subscribe(MqttSession session, List<String> topicFilters, int qos) {
		// check qos
		if (qos < 0 || qos > 2) {
			throw new IllegalArgumentException("qos is not 0, 1 or 2 : " + qos);
		}
		
		// check topic
		int[] aryqos = new int[topicFilters.size()];
		String[] topics = new String[topicFilters.size()];
		int index = 0;
		for (String strTopic : topicFilters) {
			if (strTopic == null)
				throw new NullPointerException("Null is included in the 'topicFilters' list.");
			if (strTopic.length() <= 0)
				throw new IllegalArgumentException("Empty string is included in the 'topicFilters' list.");
			aryqos[index] = qos;
			topics[index] = strTopic;
			++index;
		}
		
		session.subscribe(topics, aryqos);
	}
	
	/**
	 * 指定されたトピックに対するメッセージのサブスクライブ（購読）を終了する。
	 * サブスクライブの終了以降は、指定されたトピックについて、ここで指定したセッションにメッセージは配信されない。
	 * トピックにはワイルドカードが指定できる。
	 * @param session		使用するセッション
	 * @param topicFilter	トピック（ワイルドカードの指定も可）
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくは、サブスクライブ終了に失敗した場合
	 */
	static public void unsubscribe(MqttSession session, String topicFilter) {
		session.unsubscribe(topicFilter);
	}
	
	/**
	 * <em>topicFilters</em> に含まれるすべてのトピックに対するメッセージのサブスクライブ（購読）を終了する。
	 * サブスクライブの終了以降は、指定されたトピックについて、ここで指定したセッションにメッセージは配信されない。
	 * トピックにはワイルドカードが指定できる。
	 * @param session		使用するセッション
	 * @param topicFilters	トピック（ワイルドカードの指定も可）のリスト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合、
	 * 									もしくは、<em>topicFilters</em> の要素に空文字が含まれている場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくは、サブスクライブ終了に失敗した場合
	 */
	static public void unsubscribe(MqttSession session, List<String> topicFilters) {
		// check topic
		String[] topics = new String[topicFilters.size()];
		int index = 0;
		for (String strTopic : topicFilters) {
			if (strTopic == null)
				throw new NullPointerException("Null is included in the 'topicFilters' list.");
			if (strTopic.length() <= 0)
				throw new IllegalArgumentException("Empty string is included in the 'topicFilters' list.");
			topics[index] = strTopic;
			++index;
		}
		
		session.unsubscribe(topics);
	}

	/**
	 * バッファリングされたメッセージが一つもない場合に <tt>true</tt> を返す。
	 * @param session		使用するセッション
	 * @return	バッファリングされた受信済みメッセージが存在しない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isMessageEmpty(MqttBufferedSession session) {
		return session.isMessageEmpty();
	}

	/**
	 * バッファリングされているメッセージ数を返す。
	 * @param session		使用するセッション
	 * @return	バッファリングされている受信済みメッセージ数
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getMessageCount(MqttBufferedSession session) {
		return session.getMessageCount();
	}

	/**
	 * バッファリングされているメッセージをすべて破棄する。
	 * @param session		使用するセッション
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public void clearMessages(MqttBufferedSession session) {
		session.clearMessages();
	}

	/**
	 * 指定されたトピックに一致する受信済みメッセージがバッファリングされているかを判定する。
	 * トピックにはワイルドカードが指定できる。
	 * @param session		使用するセッション
	 * @param topicFilter	判定するトピック（ワイルドカードの指定も可）
	 * @return	トピックに一致するメッセージを受信済みの場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>session</em> が <tt>null</tt> の場合
	 */
	static public boolean containsMessage(MqttBufferedSession session, String topicFilter) {
		return session.containsMessage(topicFilter);
	}

	/**
	 * <em>topicFilters</em> に含まれるすべてのトピックに一致する受信済みメッセージがバッファリングされているかを判定する。
	 * <em>topicFilters</em> の要素が空の場合、このメソッドは <tt>false</tt> を返す。
	 * @param session		使用するセッション
	 * @param topicFilters	判定対象のトピック（ワイルドカードの指定も可）のリスト
	 * @return	バッファリングされている受信済みメッセージが <em>topicFilters</em> のすべての要素と一致した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>session</em>、<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 */
	static public boolean containsAllMessages(MqttBufferedSession session, List<String> topicFilters) {
		return session.containsAllMessages(topicFilters);
	}
	
	/**
	 * バッファリングされた受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>このメソッドでは、受信済みメッセージは削除されない。
	 * @param session		使用するセッション
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public void waitMessage(MqttBufferedSession session) throws InterruptedException
	{
		session.waitMessage();
	}
	
	/**
	 * <em>topicFilter</em> に指定されたトピックに一致するバッファリングされた受信済みメッセージが存在しない場合、
	 * 指定されたトピックに一致するメッセージを受信するまで処理をブロックする。
	 * <p>このメソッドでは、受信済みメッセージは削除されない。
	 * @param session		使用するセッション
	 * @param topicFilter		待機対象のトピック（ワイルドカードの指定も可）
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public void waitMessage(MqttBufferedSession session, String topicFilter)
	throws InterruptedException
	{
		session.waitMessage(topicFilter);
	}

	/**
	 * <em>topicFilters</em> に指定されたすべてのトピックパターンに一致するメッセージがすべて受信されるまで、処理をブロックする。
	 * <em>topicFilters</em> の要素が空の場合、このメソッドは何らかのメッセージを受信するまで、処理をブロックする。
	 * <p>このメソッドでは、受信済みメッセージは削除されない。
	 * @param session		使用するセッション
	 * @param topicFilters		待機対象のトピック（ワイルドカードの指定も可）のリスト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合、もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public void waitAllMessages(MqttBufferedSession session, List<String> topicFilters)
	throws InterruptedException
	{
		session.waitAllMessages(topicFilters);
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @return	取得した受信済みメッセージを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public MqttArrivedMessage popMessage(MqttBufferedSession session) throws InterruptedException
	{
		return session.popMessage();
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致するもっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @param topicFilter		取得対象のトピック（ワイルドカードの指定も可）
	 * @return	取得した受信済みメッセージを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public MqttArrivedMessage popFilteredMessage(MqttBufferedSession session, String topicFilter)
	throws InterruptedException
	{
		return session.popFilteredMessage(topicFilter);
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得し、文字列に変換する。
	 * 受信したメッセージを文字列に変換する際、指定された文字コードで変換する。
	 * <em>charsetName</em> が <tt>null</tt> もしくは空文字の場合、プラットフォーム標準の文字コードで変換する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @param charsetName	文字コード名
	 * @return	文字列に変換されたメッセージを返す。
	 * @throws NullPointerException	<em>session</em> が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくは、指定された文字コードがサポートされていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public String popString(MqttBufferedSession session, String charsetName) throws InterruptedException
	{
		String ret = null;
		MqttArrivedMessage msg = session.popMessage();
		if (msg != null) {
			ret = msg.getMessageString(charsetName);
		} else {
			ret = "";
		}
		return ret;
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得し、文字列に変換する。
	 * 受信したメッセージを文字列に変換する際、プラットフォーム標準の文字コードで変換する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @return	文字列に変換されたメッセージを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public String popString(MqttBufferedSession session) throws InterruptedException
	{
		return popString(session, null);
	}

	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得し、文字列に変換する。
	 * 受信したメッセージを文字列に変換する際、<code>Windows</code> 標準のシフトJIS(MS932) の文字コードで変換する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、セッションオブジェクトから削除される。
	 * @param session		使用するセッション
	 * @return	文字列に変換されたメッセージを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくは、文字コードがサポートされていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public String popStringSJIS(MqttBufferedSession session) throws InterruptedException
	{
		return popString(session, "MS932");
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得し、文字列に変換する。
	 * 受信したメッセージを文字列に変換する際、<code>UTF-8</code> で変換する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @return	文字列に変換されたメッセージを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくは、文字コードがサポートされていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public String popStringUTF8(MqttBufferedSession session) throws InterruptedException
	{
		return popString(session, "UTF-8");
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致する
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得し、文字列に変換する。
	 * 受信したメッセージを文字列に変換する際、指定された文字コードで変換する。
	 * <em>charsetName</em> が <tt>null</tt> もしくは空文字の場合、プラットフォーム標準の文字コードで変換する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @param topicFilter	取得対象のトピック（ワイルドカードの指定も可）
	 * @param charsetName	文字コード名
	 * @return	文字列に変換されたメッセージを返す。
	 * @throws NullPointerException	<em>session</em>、<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくは、指定された文字コードがサポートされていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public String popFilteredString(MqttBufferedSession session, String topicFilter, String charsetName)
	throws InterruptedException
	{
		String ret = null;
		MqttArrivedMessage msg = session.popFilteredMessage(topicFilter);
		if (msg != null) {
			ret = msg.getMessageString(charsetName);
		} else {
			ret = "";
		}
		return ret;
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致する
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得し、文字列に変換する。
	 * 受信したメッセージを文字列に変換する際、プラットフォーム標準の文字コードで変換する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @param topicFilter	取得対象のトピック（ワイルドカードの指定も可）
	 * @return	文字列に変換されたメッセージを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public String popFilteredString(MqttBufferedSession session, String topicFilter)
	throws InterruptedException
	{
		return popFilteredString(session, topicFilter, null);
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致する
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得し、文字列に変換する。
	 * 受信したメッセージを文字列に変換する際、<code>Windows</code> 標準のシフトJIS(MS932) の文字コードで変換する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @param topicFilter	取得対象のトピック（ワイルドカードの指定も可）
	 * @return	文字列に変換されたメッセージを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくは、文字コードがサポートされていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public String popFilteredStringSJIS(MqttBufferedSession session, String topicFilter)
	throws InterruptedException
	{
		return popFilteredString(session, topicFilter, "MS932");
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致する
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得し、文字列に変換する。
	 * 受信したメッセージを文字列に変換する際、<code>UTF-8</code> で変換する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @param topicFilter	取得対象のトピック（ワイルドカードの指定も可）
	 * @return	文字列に変換されたメッセージを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくは、文字コードがサポートされていない場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public String popFilteredStringUTF8(MqttBufferedSession session, String topicFilter)
	throws InterruptedException
	{
		return popFilteredString(session, topicFilter, "UTF-8");
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得し、指定されたファイルにそのまま保存する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session	使用するセッション
	 * @param filename	出力先のファイル名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくはファイル出力に失敗した場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public void popBinaryFile(MqttBufferedSession session, String filename)
	throws InterruptedException
	{
		MqttArrivedMessage msg = session.popMessage();
		msg.saveMessageToBinaryFile(filename);
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致する
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得し、指定されたファイルにそのまま保存する。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @param topicFilter	取得対象のトピック（ワイルドカードの指定も可）
	 * @param filename	出力先のファイル名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、もしくはファイル出力に失敗した場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public void popFilteredBinaryFile(MqttBufferedSession session, String topicFilter, String filename)
	throws InterruptedException
	{
		MqttArrivedMessage msg = session.popFilteredMessage(topicFilter);
		msg.saveMessageToBinaryFile(filename);
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得し、指定されたファイルに保存する。
	 * ファイル保存の際、メッセージを <em>messageCharsetName</em> に指定された文字コードで文字列に変換し、
	 * <em>fileCharsetName</em> に指定された文字コードでファイルに出力する。
	 * <em>messageCharsetName</em> および <em>fileCharsetName</em> は、
	 * 指定された文字列が <tt>null</tt> もしくは空文字の場合、プラットフォーム標準の文字コードが使用される。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session	使用するセッション
	 * @param filename	出力先のファイル名
	 * @param messageCharsetName	メッセージを文字列に変換する際の文字コード名
	 * @param fileCharsetName		ファイル出力時の文字コード名
	 * @throws NullPointerException	<em>session</em>、<em>filename</em> が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、指定された文字コードがサポートされていない場合、もしくはファイル出力に失敗した場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public void popTextFile(MqttBufferedSession session, String filename, String messageCharsetName, String fileCharsetName)
	throws InterruptedException
	{
		MqttArrivedMessage msg = session.popMessage();
		msg.saveMessageToTextFile(filename, messageCharsetName, fileCharsetName);
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、<em>topicFilter</em> に指定されたトピックと一致する
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得し、指定されたファイルに保存する。
	 * ファイル保存の際、メッセージを <em>messageCharsetName</em> に指定された文字コードで文字列に変換し、
	 * <em>fileCharsetName</em> に指定された文字コードでファイルに出力する。
	 * <em>messageCharsetName</em> および <em>fileCharsetName</em> は、
	 * 指定された文字列が <tt>null</tt> もしくは空文字の場合、プラットフォーム標準の文字コードが使用される。
	 * 受信済みメッセージが存在しない場合、<em>topicFilter</em> に一致するトピックのメッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、バッファから削除される。
	 * @param session		使用するセッション
	 * @param topicFilter	取得対象のトピック（ワイルドカードの指定も可）
	 * @param filename	出力先のファイル名
	 * @param messageCharsetName	メッセージを文字列に変換する際の文字コード名
	 * @param fileCharsetName		ファイル出力時の文字コード名
	 * @throws NullPointerException	<em>session</em>、<em>topicFilter</em>、<em>filename</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 * @throws MqttRuntimeException	セッションがサーバーに接続されていない場合、指定された文字コードがサポートされていない場合、もしくはファイル出力に失敗した場合 
	 * @throws InterruptedException	現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 								この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	static public void popFilteredTextFile(MqttBufferedSession session, String topicFilter, String filename, String messageCharsetName, String fileCharsetName)
	throws InterruptedException
	{
		MqttArrivedMessage msg = session.popFilteredMessage(topicFilter);
		msg.saveMessageToTextFile(filename, messageCharsetName, fileCharsetName);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
