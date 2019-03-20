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
 * @(#)MqttBufferedSessionImpl.java	0.3.0	2013/06/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttBufferedSessionImpl.java	0.2.0	2013/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttBufferedSession;
import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.MqttException;
import ssac.aadl.runtime.mqtt.MqttRuntimeException;

/**
 * サブスクライブしたメッセージをバッファリングする、MQTT セッション・インタフェースの実装。
 * 
 * @version 0.3.0	2013/06/27
 * @since 0.3.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttBufferedSessionImpl extends MqttManagedSessionImpl implements MqttBufferedSession
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final Pattern _emptyPattern = Pattern.compile("", Pattern.DOTALL);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected volatile boolean	_disconnected = true;

	/** キャッシュ同期オブジェクト **/
	protected Object	_lockCache = new Object();		// inner lock
	/** バッファ同期オブジェクト **/
	protected Object	_lockBuffer = new Object();		// outer lock

	/** 受信メッセージキャッシュ **/
	protected LinkedList<MqttArrivedMessage>	_listCache = new LinkedList<MqttArrivedMessage>();
	/** 受信済みメッセージプール **/
	protected LinkedList<MqttArrivedMessage>	_listBuffer  = new LinkedList<MqttArrivedMessage>();

	/** ワイルドカード含むトピックの正規表現パターンキャッシュ **/
	protected WeakHashMap<String, Pattern>	_cacheTopicPattern = new WeakHashMap<String, Pattern>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された接続パラメータでサーバーに接続し、新しいセッションを開始する。
	 * @param params	接続パラメータ
	 * @throws NullPointerException	<em>params</em> が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	接続エラーが発生した場合
	 */
	public MqttBufferedSessionImpl(MqttConnectionParams params) {
		super(params);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * バッファリングされたメッセージが一つもない場合に <tt>true</tt> を返す。
	 * @return	バッファリングされた受信済みメッセージが存在しない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isMessageEmpty() {
		synchronized (_lockBuffer) {
			if (_listBuffer.isEmpty()) {
				synchronized (_lockCache) {
					return _listCache.isEmpty();
				}
			} else {
				return false;
			}
		}
	}

	/**
	 * バッファリングされているメッセージ数を返す。
	 * @return	バッファリングされている受信済みメッセージ数
	 */
	public int getMessageCount() {
		synchronized (_lockBuffer) {
			int len = _listBuffer.size();
			synchronized (_lockCache) {
				len += _listCache.size();
			}
			return len;
		}
	}

	/**
	 * バッファリングされているメッセージをすべて破棄する。
	 */
	public void clearMessages() {
		synchronized (_lockBuffer) {
			_listBuffer.clear();
			synchronized (_lockCache) {
				_listCache.clear();
			}
		}
	}

	/**
	 * 指定されたトピックに一致する受信済みメッセージがバッファリングされているかを判定する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilter	判定するトピック（ワイルドカードの指定も可）
	 * @return	トピックに一致するメッセージを受信済みの場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsMessage(String topicFilter) {
		MqttArrivedMessage retmsg = null;
		synchronized (_lockBuffer) {
			copyMessageNoWait();
			if (topicFilter != null) {
				Pattern topicPattern = cachedTopicPattern(topicFilter);
				retmsg = getMatchedMessageFromList(0, topicFilter, topicPattern, false);
			}
		}
		return (retmsg != null);
	}

	/**
	 * <em>topicFilters</em> に含まれるすべてのトピックに一致する受信済みメッセージがバッファリングされているかを判定する。
	 * <em>topicFilters</em> の要素が空の場合、このメソッドは <tt>false</tt> を返す。
	 * @param topicFilters	判定対象のトピック（ワイルドカードの指定も可）のリスト
	 * @return	バッファリングされている受信済みメッセージが <em>topicFilters</em> のすべての要素と一致した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 */
	public boolean containsAllMessages(List<String> topicFilters) {
		// check
		if (topicFilters.isEmpty())
			return false;
		
		// matches
		synchronized (_lockBuffer) {
			copyMessageNoWait();

			Pattern topicPattern;
			for (String strTopic : topicFilters) {
				if (strTopic == null)
					throw new NullPointerException("Null is included in the 'topicFilters' list.");
				if (strTopic.length() <= 0)
					throw new IllegalArgumentException("Empty string is included in the 'topicFilters' list.");
				topicPattern = cachedTopicPattern(strTopic);
				//--- find
				if (getMatchedMessageFromList(0, strTopic, topicPattern, false) == null) {
					//--- no matches
					return false;
				}
			}
		}
		//--- all matched
		return true;
	}
	
	/**
	 * 指定されたトピックに一致する受信済みメッセージ以外を、バッファからすべて削除する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilter	バッファに残すトピック（ワイルドカードの指定も可）
	 * @return	この呼び出しによりバッファの内容が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>topicFilter</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilter</em> が空文字の場合 
	 */
	public boolean retainMessage(String topicFilter) {
		validTopicFilter(topicFilter);
		Pattern topicPattern = cachedTopicPattern(topicFilter);

		boolean modified = false;
		synchronized (_lockBuffer) {
			copyMessageNoWait();
			String msgTopic;
			Iterator<MqttArrivedMessage> it = _listBuffer.iterator();
			if (topicPattern == _emptyPattern) {
				// ワイルドカードを含まないトピック文字列
				for (; it.hasNext(); ) {
					msgTopic = it.next().getTopic();
					if (!topicFilter.equals(msgTopic)) {
						// 一致しないものをリストから削除
						it.remove();
						modified = true;
					}
				}
			} else {
				// 正規表現パターン
				for (; it.hasNext(); ) {
					msgTopic = it.next().getTopic();
					if (!topicPattern.matcher(msgTopic).matches()) {
						// 一致しないものをリストから削除
						it.remove();
						modified = true;
					}
				}
			}
		}
		return modified;
	}
	
	/**
	 * <em>topicFilters</em> に含まれるトピックに一致する受信済みメッセージ以外を、バッファからすべて削除する。
	 * <em>topicFilters</em> の要素が空の場合、すべての受信済みメッセージが削除される。
	 * @param topicFilters	バッファに残すトピック（ワイルドカードの指定も可）のリスト
	 * @return	この呼び出しによりバッファの内容が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 */
	public boolean retainAllMessages(List<String> topicFilters) {
		// check empty
		if (topicFilters.isEmpty()) {
			boolean modified;
			synchronized (_lockBuffer) {
				copyMessageNoWait();
				modified = !_listBuffer.isEmpty();
				_listBuffer.clear();
			}
			return modified;
		}
		
		// check
		HashSet<String> stringSet = new HashSet<String>();
		ArrayList<Pattern> patternList = new ArrayList<Pattern>();
		for (String strTopic : topicFilters) {
			if (strTopic == null)
				throw new NullPointerException("Null is included in the 'topicFilters' list.");
			if (strTopic.length() <= 0)
				throw new IllegalArgumentException("Empty string is included in the 'topicFilters' list.");
			Pattern topicPattern = cachedTopicPattern(strTopic);
			if (topicPattern == _emptyPattern)
				stringSet.add(strTopic);
			else
				patternList.add(topicPattern);
		}

		boolean modified = false;
		synchronized (_lockBuffer) {
			copyMessageNoWait();
			String msgTopic;
			Iterator<MqttArrivedMessage> it = _listBuffer.iterator();
			OuterLoop : for (; it.hasNext(); ) {
				msgTopic = it.next().getTopic();
				if (!stringSet.isEmpty() && stringSet.contains(msgTopic)) {
					// 一致したメッセージは残す
					continue;
				}
				else if (!patternList.isEmpty()) {
					for (Pattern topicPattern : patternList) {
						if (topicPattern.matcher(msgTopic).matches()) {
							// 一致したメッセージは残す
							continue OuterLoop;
						}
					}
				}
				//--- 一致しないものは削除
				it.remove();
				modified = true;
			}
		}
		stringSet.clear();
		patternList.clear();
		return modified;
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * @param remove		取得したメッセージを受信済みメッセージから除去する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage getMessage(boolean remove) throws InterruptedException
	{
		// wait infinite
		return getMessageAndWait(0L, remove);
	}
	
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
	public MqttArrivedMessage getMessage(long timeout, boolean remove) throws InterruptedException
	{
		if (timeout == 0L) {
			// no wait
			return getMessageNoWait(remove);
		}
		else if (timeout < 0L) {
			// wait infinite
			return getMessageAndWait(0L, remove);
		}
		else {
			// wait until timeout
			return getMessageAndWait(timeout, remove);
		}
	}
	
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
	public MqttArrivedMessage getFilteredMessage(String topicFilter, boolean remove) throws InterruptedException
	{
		validTopicFilter(topicFilter);
		Pattern topicPattern = cachedTopicPattern(topicFilter);
		// wait infinite
		return getMatchedMessageAndWait(topicFilter, topicPattern, 0L, remove);
	}
	
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
	public MqttArrivedMessage getFilteredMessage(String topicFilter, long timeout, boolean remove) throws InterruptedException
	{
		validTopicFilter(topicFilter);
		Pattern topicPattern = cachedTopicPattern(topicFilter);
		if (timeout == 0L) {
			// no wait
			return getMatchedMessageNoWait(topicFilter, topicPattern, remove);
		}
		else if (timeout < 0L) {
			// wait infinite
			return getMatchedMessageAndWait(topicFilter, topicPattern, 0L, remove);
		}
		else {
			// wait until timeout
			return getMatchedMessageAndWait(topicFilter, topicPattern, timeout, remove);
		}
	}
	
	/**
	 * バッファリングされた受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public void waitMessage() throws InterruptedException
	{
		getMessage(false);	// wait infinite
	}
	
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
	public boolean waitMessage(long timeout) throws InterruptedException
	{
		return (getMessage(timeout, false) != null);
	}
	
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
	public void waitMessage(String topicFilter) throws InterruptedException
	{
		getFilteredMessage(topicFilter, false);	// wait infinite
	}
	
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
	public boolean waitMessage(String topicFilter, long timeout) throws InterruptedException
	{
		return (getFilteredMessage(topicFilter, timeout, false) != null);
	}

	/**
	 * <em>topicFilters</em> に含まれるトピックに一致するメッセージをすべて受信するまで、処理をブロックする。
	 * <em>topicFilters</em> の要素が空の場合、このメソッドは何らかのメッセージを受信するまで、処理をブロックする。
	 * @param topicFilters		待機対象のトピック（ワイルドカードの指定も可）のリスト
	 * @throws NullPointerException	<em>topicFilters</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>topicFilters</em> の要素に <tt>null</tt> が含まれている場合
	 * @throws IllegalArgumentException	<em>topicFilters</em> の要素に空文字が含まれている場合
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public void waitAllMessages(List<String> topicFilters) throws InterruptedException
	{
		waitAllMessages(topicFilters, -1);	// wait infinite
	}
	
	/**
	 * <em>topicFilters</em> に含まれるトピックに一致するメッセージをすべて受信するか、
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
	public boolean waitAllMessages(List<String> topicFilters, long timeout) throws InterruptedException
	{
		if (topicFilters.size() <= 0) {
			// 要素が空の場合は、どのメッセージでも応答する。
			return (getMessage(timeout, false) != null);
		}
		
		// チェックリスト生成
		HashMap<String, Pattern> map = new HashMap<String, Pattern>();
		for (String strTopic : topicFilters) {
			if (strTopic == null)
				throw new NullPointerException("Null is included in the 'topicFilters' list.");
			if (strTopic.length() <= 0)
				throw new IllegalArgumentException("Empty string is included in the 'topicFilters' list.");
			if (!map.containsKey(strTopic)) {
				Pattern pat = cachedTopicPattern(strTopic);
				map.put(strTopic, pat);
			}
		}
		if (map.isEmpty()) {
			// 有効なトピック文字列がない場合は、どのメッセージでも応答する。
			return (getMessage(timeout, false) != null);
		}
		
		// 受信待機
		//--- waitAllMessages は特殊なメソッドとし、他のリスト処理をすべてブロックする。
		ensureConnectedForWaitMessage();
		synchronized (_lockBuffer) {
			// 既存の受信済みメッセージの検索
			copyMessageNoWait();
			if (removeMatchedPatternsFromMapByList(0, map)) {
				// 既存メッセージのみですべて一致
				ensureConnectedForWaitMessage();
				return true;
			}
			
			// 新規メッセージの受信待機
			ensureConnectedForWaitMessage();
			int beginIndex = _listBuffer.size();	// 既存リストには一致するものがなかったので、新規受信分から検索
			if (timeout > 0L) {
				// wait until timeout
				long startTime = System.currentTimeMillis();
				long endTime = startTime;
				long passedTime = endTime - startTime;
				for (; !_disconnected && !map.isEmpty() && passedTime >= 0L && passedTime < timeout; ) {
					if (copyMessageAndWait(timeout - passedTime)) {
						if (removeMatchedPatternsFromMapByList(beginIndex, map)) {
							// すべて一致のため、検索終了
							break;
						}
						beginIndex = _listBuffer.size();
					}
					endTime = System.currentTimeMillis();
					passedTime = endTime - startTime;
				}
			}
			else if (timeout < 0L) {
				// wait infinit
				for (; !_disconnected && !map.isEmpty(); ) {
					if (copyMessageAndWait(0L)) {
						if (removeMatchedPatternsFromMapByList(beginIndex, map)) {
							// すべて一致のため、検索終了
							break;
						}
						beginIndex = _listBuffer.size();
					}
				}
			}
			// else : no wait : 新しいメッセージは取得せず、処理終了
		}
		ensureConnectedForWaitMessage();
		return (map.isEmpty());	// パターンがすべて削除されていれば、全メッセージ受信済み
	}
	
	/**
	 * バッファリングされた受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するまで処理をブロックする。
	 * <p>取得したメッセージは、このオブジェクトから削除される。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに割り込んだ場合。
	 * 									この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	public MqttArrivedMessage popMessage() throws InterruptedException
	{
		return getMessage(true);
	}
	
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
	public MqttArrivedMessage popMessage(long timeout) throws InterruptedException
	{
		return getMessage(timeout, true);
	}
	
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
	public MqttArrivedMessage popFilteredMessage(String topicFilter) throws InterruptedException
	{
		return getFilteredMessage(topicFilter, true);
	}
	
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
	public MqttArrivedMessage popFilteredMessage(String topicFilter, long timeout) throws InterruptedException
	{
		return getFilteredMessage(topicFilter, timeout, true);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void onMessageArrived(MqttArrivedMessage message) throws Exception
	{
		synchronized (_lockCache) {
			_listCache.add(message);
			_lockCache.notifyAll();	// 待機スレッドの再開
		}
		super.onMessageArrived(message);
	}

	protected void ensureConnectedForWaitMessage() {
		// check connecting
		if (_disconnected) {
			Throwable ex = getConnectionLostCause();
			if (ex == null) {
				ex = new MqttException("MQTT client not connected!");
			}
			throw new MqttRuntimeException("Failed to get arrived message.", ex, _mqttParams);
		}
	}
	
	static protected void validTopicFilter(String topicFilter) {
		if (topicFilter.length() <= 0)
			throw new IllegalArgumentException("topicFilter string is empty.");
	}

	@Override
	protected void onConnected() {
		_disconnected = false;
		super.onConnected();
	}

	@Override
	protected void onDisconnected() {
		_disconnected = true;
		synchronized (_lockCache) {
			_lockCache.notifyAll();	// 待機スレッドの再開
		}
		super.onDisconnected();
	}

	@Override
	protected void onConnectionLost(Throwable cause) {
		_disconnected = !isConnected();
		super.onConnectionLost(cause);
		synchronized (_lockCache) {
			_lockCache.notifyAll();	// 待機スレッドの再開
		}
	}

	/**
	 * 指定されたマップから、バッファリングされた受信済みメッセージのトピックと一致するトピックパターンをすべて除去する。
	 * バッファリングされた受信済みメッセージの検索は、指定されたインデックスから開始する。
	 * @param beginIndex	検索開始位置とする受信済みメッセージリストのインデックス
	 * @param map	トピックパターンのマップ
	 * @return	マップの要素が空となった場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	<em>beginIndex</em> が範囲外の場合
	 */
	protected boolean removeMatchedPatternsFromMapByList(int beginIndex, Map<String, Pattern> map) {
		// 指定位置インデックスのリストイテレータを取得
		Iterator<MqttArrivedMessage> lit = _listBuffer.listIterator(beginIndex);
		if (!lit.hasNext()) {
			// インデックス以降の要素が存在しないので、処理終了
			return (map.isEmpty());
		}
		
		// first step
		//--- バッファリストの指定位置から検索し、固定トピックと一致するものをマップから除去
		for (; lit.hasNext(); ) {
			// 受信メッセージのトピックと一致するキーをマップから削除
			//--- 受信メッセージのトピックを使用するので、ワイルドカードは含まれていない
			map.remove(lit.next().getTopic());
		}
		if (map.isEmpty()) {
			return true;
		}
		
		// next step
		//--- マップの値（パターン）と一致するトピックを、リストの指定位置から検索し、一致した場合マップから除去
		Iterator<Map.Entry<String, Pattern>> mit = map.entrySet().iterator();
		Map.Entry<String, Pattern> entry;
		Pattern topicPattern;
		for (; mit.hasNext(); ) {
			entry = mit.next();
			topicPattern = entry.getValue();
			if (topicPattern != _emptyPattern) {
				//--- トピックパターン（正規表現）のみに限定
				for (lit = _listBuffer.listIterator(beginIndex); lit.hasNext(); ) {
					if (topicPattern.matcher(lit.next().getTopic()).matches()) {
						// トピックと一致したパターンは、マップから除去
						mit.remove();
						break;
					}
				}
			}
		}
		
		// マップが空になったら true
		return (map.isEmpty());
	}
	
	/**
	 * 受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、処理をブロックせずに即座に制御を返す。
	 * @param removeFromList	取得したメッセージを受信済みメッセージから除去する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 */
	protected MqttArrivedMessage getMessageNoWait(boolean removeFromList) {
		synchronized (_lockBuffer) {
			copyMessageNoWait();
			MqttArrivedMessage retmsg = getMessageFromList(removeFromList);
			ensureConnectedForWaitMessage();
			return retmsg;
		}
	}
	
	/**
	 * 受信済みメッセージから、もっとも古い（先頭の）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、メッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に 0 を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * <p>なお、待機中にサーバーとの接続が切断されたとき、このメソッドは待機を中断して <tt>null</tt> を返す。
	 * @param timeout			待機時間をミリ秒で指定する。0 を指定した場合はメッセージを受信するまで待機する。
	 * @param removeFromList	取得したメッセージを受信済みメッセージから除去する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws IllegalArgumentException	<em>timeout</em> の値が負である場合 
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに
	 * 										割り込んだ場合。この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	protected MqttArrivedMessage getMessageAndWait(long timeout, boolean removeFromList) throws InterruptedException
	{
		assert (timeout >= 0L);

		MqttArrivedMessage retmsg;
		synchronized (_lockBuffer) {
			// 既存のリストを検索
			copyMessageNoWait();
			retmsg = getMessageFromList(removeFromList);
			ensureConnectedForWaitMessage();
			if (retmsg != null) {
				return retmsg;
			}

			// 受信待機
			if (timeout > 0L) {
				// wait with timeout
				long startTime = System.currentTimeMillis();
				long endTime = startTime;
				long passedTime = endTime - startTime;
				for (; !_disconnected && passedTime >= 0L && passedTime < timeout; ) {
					if (copyMessageAndWait(timeout - passedTime)) {
						retmsg = getMessageFromList(removeFromList);
						if (retmsg != null)
							break;
					}
					endTime = System.currentTimeMillis();
					passedTime = endTime - startTime;
				}
			}
			else {
				// wait infinit
				for (; !_disconnected ;) {
					if (copyMessageAndWait(0L)) {
						retmsg = getMessageFromList(removeFromList);
						if (retmsg != null)
							break;
					}
				}
			}
			ensureConnectedForWaitMessage();
		}
		return retmsg;
	}
	
	/**
	 * 受信済みメッセージから、指定されたトピックパターンと一致するトピックを持つ、
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得する。
	 * <p>受信済みメッセージが存在しない場合、このメソッドは即座に制御を返す。
	 * @param topicString		取得対象のワイルドカードを含まないトピック文字列
	 * @param topicPattern		取得対象のトピックと比較する正規表現
	 * @param removeFromList	取得したメッセージを受信済みメッセージリストから削除する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>topicString</em> もしくは <em>topicPattern</em> が <tt>null</tt> の場合
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 */
	protected MqttArrivedMessage getMatchedMessageNoWait(String topicString, Pattern topicPattern, boolean removeFromList) {
		synchronized (_lockBuffer) {
			copyMessageNoWait();
			MqttArrivedMessage retmsg = getMatchedMessageFromList(0, topicString, topicPattern, removeFromList);
			ensureConnectedForWaitMessage();
			return retmsg;
		}
	}

	/**
	 * 受信済みメッセージから、指定されたトピックパターンに一致するトピックを持つ、
	 * もっとも古い（先頭に近い）受信済みメッセージを 1 つだけ取得する。
	 * 受信済みメッセージが存在しない場合、トピックパターンに一致するトピックのメッセージを受信するか、
	 * <em>timeout</em> に指定された時間が経過するまで、処理をブロックする。
	 * <em>timeout</em> に 0 を指定した場合は、メッセージを受信するまで処理をブロックする。
	 * @param topicString		取得対象のワイルドカードを含まないトピック文字列
	 * @param topicPattern		取得対象のトピックと比較する正規表現
	 * @param timeout			待機時間をミリ秒で指定する。0 を指定した場合はメッセージを受信するまで待機する。
	 * @param removeFromList	取得したメッセージを受信済みメッセージリストから削除する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>topicString</em> もしくは <em>topicPattern</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>timeout</em> の値が負である場合
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに
	 * 										割り込んだ場合。この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	protected MqttArrivedMessage getMatchedMessageAndWait(String topicString, Pattern topicPattern, long timeout, boolean removeFromList) throws InterruptedException
	{
		assert (timeout >= 0L);

		MqttArrivedMessage retmsg;
		synchronized (_lockBuffer) {
			// 現在のリストを検索
			copyMessageNoWait();
			retmsg = getMatchedMessageFromList(0, topicString, topicPattern, removeFromList);
			ensureConnectedForWaitMessage();
			if (retmsg != null) {
				return retmsg;
			}
			int beginIndex = _listBuffer.size();

			// メッセージ受信待ち
			if (timeout > 0L) {
				// wait until timeout
				long startTime = System.currentTimeMillis();
				long endTime = startTime;
				long passedTime = endTime - startTime;
				for (; !_disconnected && passedTime >= 0L && passedTime < timeout; ) {
					if (copyMessageAndWait(timeout - passedTime)) {
						retmsg = getMatchedMessageFromList(beginIndex, topicString, topicPattern, removeFromList);
						if (retmsg != null)
							break;
						beginIndex = _listBuffer.size();
					}
					endTime = System.currentTimeMillis();
					passedTime = endTime - startTime;
				}
			}
			else {
				// wait infinit
				for (; !_disconnected ;) {
					if (copyMessageAndWait(0L)) {
						retmsg = getMatchedMessageFromList(beginIndex, topicString, topicPattern, removeFromList);
						if (retmsg != null)
							break;
						beginIndex = _listBuffer.size();
					}
				}
			}
			ensureConnectedForWaitMessage();
		}
		return retmsg;
	}
	
	/**
	 * 受信済みメッセージキャッシュから内部リストへメッセージをコピーする。
	 * コピーする受信済みメッセージが存在しない場合でも、このメソッドは待機せず、即座に制御を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、<code>_listBuffer</code> オブジェクトに対して同期化されていない。
	 * </blockquote>
	 * @return	受信済みメッセージが存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	protected boolean copyMessageNoWait() {
		synchronized (_lockCache) {
			if (!_listCache.isEmpty()) {
				_listBuffer.addAll(_listCache);
				_listCache.clear();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 受信済みメッセージキャッシュから内部リストへメッセージをコピーする。
	 * コピーする受信済みメッセージが存在しない場合は、指定された時間待機する。
	 * <p>このメソッドが <tt>false</tt> を返す場合、待機時間が経過した場合、
	 * システムにより予期せぬ割り込みが発生した場合、接続が切断された場合などの可能性がある。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、<code>_listBuffer</code> オブジェクトに対して同期化されていない。
	 * </blockquote>
	 * @param timeout	待機時間をミリ秒で指定する。0 を指定した場合はメッセージを受信するまで待機する。
	 * @return	受信済みメッセージが存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 * @throws IllegalArgumentException	<em>timeout</em> の値が負である場合
	 * @throws MqttRuntimeException		セッションが接続されていない場合 
	 * @throws InterruptedException		現在のスレッドが通知を待機する前または待機中に、いずれかのスレッドが現在のスレッドに
	 * 										割り込んだ場合。この例外スローされると、現在のスレッドの「割り込みステータス」はクリアされる。
	 */
	protected boolean copyMessageAndWait(long timeout) throws InterruptedException
	{
		assert (timeout >= 0L);
		synchronized (_lockCache) {
			if (!_listCache.isEmpty()) {
				_listBuffer.addAll(_listCache);
				_listCache.clear();
				return true;
			}

			// wait
			ensureConnectedForWaitMessage();
			_lockCache.wait(timeout);
			
			// check
			if (!_listCache.isEmpty()) {
				_listBuffer.addAll(_listCache);
				_listCache.clear();
				return true;
			}
		}
		return false;
	}

	/**
	 * 受信済みメッセージリストの先頭から、受信済みメッセージを 1 つだけ取得する。
	 * <p>このメソッドでは、受信済みメッセージリストのみを対象としており、
	 * 受信済みメッセージキャッシュの状態は考慮しない。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、<code>_listBuffer</code> オブジェクトに対して同期化されていない。
	 * </blockquote>
	 * @param removeFromList	取得したメッセージを受信済みメッセージリストから削除する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 */
	protected MqttArrivedMessage getMessageFromList(boolean removeFromList) {
		// 既存のリストから取得
		if (!_listBuffer.isEmpty()) {
			if (removeFromList) {
				return _listBuffer.pop();
			} else {
				return _listBuffer.getFirst();
			}
		}
		return null;
	}
	
	/**
	 * 受信済みメッセージリストから、指定されたトピックパターンと一致するトピックを持つ、
	 * 指定されたインデックス位置から最も古い（リスト先頭に近い）受信済みメッセージを 1 つだけ取得する。
	 * <p>このメソッドでは、受信済みメッセージリストのみを対象としており、
	 * 受信済みメッセージキャッシュの状態は考慮しない。また、トピックの比較においては、大文字小文字も区別する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、<code>_listBuffer</code> オブジェクトに対して同期化されていない。
	 * </blockquote>
	 * @param beginIndex		検索開始位置を示すインデックス
	 * @param topicString		取得対象のワイルドカードを含まないトピック文字列
	 * @param topicPattern		取得対象のトピックと比較する正規表現
	 * @param removeFromList	取得したメッセージを受信済みメッセージリストから削除する場合は <tt>true</tt> を指定する。
	 * @return	取得した受信済みメッセージを返す。取得できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>topicString</em> もしくは <em>topicPattern</em> が <tt>null</tt> の場合
	 */
	protected MqttArrivedMessage getMatchedMessageFromList(int beginIndex, String topicString, Pattern topicPattern, boolean removeFromList) {
		assert (topicString != null && topicPattern != null);
		
		// 受信済みリストから検索
		// 要素が空なら、対象メッセージなし
		if (_listBuffer.isEmpty()) {
			return null;
		}
		
		// 検索開始位置となるイテレータ取得
		Iterator<MqttArrivedMessage> it;
		if (beginIndex > 0) {
			if (beginIndex < _listBuffer.size()) {
				it = _listBuffer.listIterator(beginIndex);
			} else {
				return null;	// no matched
			}
		} else {
			it = _listBuffer.iterator();
		}
		
		// リスト検索
		MqttArrivedMessage msg;
		if (topicPattern == _emptyPattern) {
			// ワイルドカードを含まないトピック文字列
			for (; it.hasNext(); ) {
				msg = it.next();
				if (topicString.equals(msg.getTopic())) {
					// 一致、必要に応じてリストから削除
					if (removeFromList) {
						it.remove();
					}
					return msg;
				}
			}
		}
		else {
			// 正規表現パターンによる検索
			for (; it.hasNext();) {
				msg = it.next();
				if (topicPattern.matcher(msg.getTopic()).matches()) {
					// 一致、必要に応じてリストから削除
					if (removeFromList) {
						it.remove();
					}
					return msg;
				}
			}
		}
		return null;
	}

	/**
	 * このオブジェクトが保持するキャッシュから、トピックに対応する正規表現パターンを取得する。
	 * トピックにワイルドカードが含まれていない場合、無効なパターンを示す <code>_emptyPattern</code> が返される。
	 * キャッシュに存在しないトピックパターンの場合は、新しい正規表現パターンを登録する。
	 * @param topicFilter	トピック（ワイルドカード含む）
	 * @return	トピックに対応する正規表現パターン、トピックが <tt>null</tt> もしくは空文字列の場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected Pattern cachedTopicPattern(String topicFilter) {
		assert (topicFilter != null);
		synchronized (_cacheTopicPattern) {
			Pattern pat = _cacheTopicPattern.get(topicFilter);
			if (pat == null) {
				if (isFilterPatternTopic(topicFilter)) {
					pat = createTopicPattern(topicFilter);
					// ※String#intern() は、ハッシュや isFilterPatternTopic() を毎回コールするよりもはるかに遅い
					_cacheTopicPattern.put(topicFilter.intern(), pat);	// 同一文字列でキャッシュするインスタンスをユニークにする
				} else {
					pat = _emptyPattern;
				}
			}
			return pat;
		}
	}

	/**
	 * トピックにワイルドカード文字が含まれているかを判定する。
	 * @param topic	トピック
	 * @return	ワイルドカードが含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static protected boolean isFilterPatternTopic(String topic) {
		return ((topic.indexOf('+') >= 0) || (topic.indexOf('#') >= 0));
	}

	/**
	 * ワイルドカードを含むトピック文字列から、正規表現パターンを生成する。
	 * @param topic	トピック
	 * @return	正規表現パターン
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static protected Pattern createTopicPattern(String topic) {
		if (topic.startsWith("#")) {
			return Pattern.compile("[^/]*(?:/[^/]*)*", Pattern.DOTALL);
		}
		topic = Pattern.quote(topic);
		int len = topic.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char ch = topic.charAt(i);
			if (ch == '+') {
				sb.append("\\E[^/]+\\Q");
			}
			else if (ch == '#') {
				if (sb.length() > 0 && sb.charAt(sb.length()-1) == '/') {
					sb.delete(sb.length()-1, sb.length());
				}
				sb.append("\\E(?:/[^/]*)*");
				break;
			}
			else {
				sb.append(ch);
			}
		}
		
		return Pattern.compile(sb.toString(), Pattern.DOTALL);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
