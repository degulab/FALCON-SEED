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
 * @(#)AADLMqttFileSubscriber.java	0.3.0	2013/06/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMqttFileSubscriber.java	0.2.0	2013/04/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMqttFileSubscriber.java	0.1.0	2013/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.internal;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Timestamp;

import ssac.aadl.runtime.mqtt.MqPayload;
import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.MqttSession;
import ssac.aadl.runtime.mqtt.MqttUtil;

/**
 * AADL 専用の ファイル Subscriber クラス。
 * 
 * @version 0.3.0	2013/06/27
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class AADLMqttFileSubscriber extends AADLMqttClient
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String MSG_TERMINATED_SUBSCRIPTION = "Subscription was terminated!";
	static protected final String MSG_TIMEOUT_SUBSCRIPTION = "Timed out subscription!";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected String		_topic;
	protected int			_qos;
	protected String		_msgEncoding;
	protected String		_fileEncoding;
	protected File			_targetFile;
	protected long			_recvTime;
	protected long			_recvTimeout;
	protected BigDecimal	_storedCount;
	protected BigDecimal	_droppedCount;
	protected BigDecimal	_storedBytes;
	protected BigDecimal	_droppedBytes;
	protected BigDecimal	_recvCount;
	protected BigDecimal	_recvLimit;
	protected String		_strAppendLineSep;
	protected byte[]		_binAppendLineSep;
	protected boolean		_wasTimeout = false;
	protected boolean		_completed = false;
	
	protected OutputStream		_oStream = null;
	protected BufferedWriter	_writer = null;
	
	protected Object		_responseLock = null;
	protected Object		_streamLock   = null;
	
	private Object		_subStartEvent = null;
	
//	protected StringBuilder	_arrivedDebugBuffer;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの接続パラメータで、新しいインスタンスを生成する。
	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 */
	public AADLMqttFileSubscriber() {
		this(MqttUtil.DEFAULT_IP_ADDRESS, MqttUtil.DEFAULT_PORT_NUMBER);
	}

	/**
	 * 指定された接続パラメータで、新しいインスタンスを生成する。
	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * @param serverURI	接続先を示す URI
	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合
	 */
	public AADLMqttFileSubscriber(String serverURI) {
		this(serverURI, MqttUtil.DEFAULT_PORT_NUMBER);
	}

	/**
	 * 指定された接続パラメータで、新しいインスタンスを生成する。
	 * <em>serverURI</em> にポート番号が含まれている場合は <em>portNo</em> は無視される。
	 * ポート番号が含まれていない場合は 1883 番ポートが <em>serverURI</em> の終端に付加される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * @param serverURI	接続先を示す URI
	 * @param portNo	接続先ポート番号(0～65535)
	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>portNo</em> が範囲外の場合
	 */
	public AADLMqttFileSubscriber(String serverURI, int portNo) {
		super(serverURI, portNo, generateClientID());
	}

	/**
	 * 指定された接続パラメータで、新しいインスタンスを生成する。
	 * @param params	接続パラメータ
	 * @throws NullPointerException	<em>params</em> が <tt>null</tt> の場合
	 */
	public AADLMqttFileSubscriber(MqttConnectionParams params) {
		super(params);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public Object getSubscribeStartedEventObject(Object eventObject) {
		return _subStartEvent;
	}
	
	public void setSubscribeStartedEventObject(Object eventObject) {
		_subStartEvent = eventObject;
	}
	
	protected void notifySubscribeStarted() {
		Object eventObj = _subStartEvent;
		if (eventObj != null) {
			synchronized (eventObj) {
				eventObj.notifyAll();
			}
		}
	}
	
	/**
	 * 指定されたトピックを購読し、受信した単一メッセージをファイルに保存する。<br>
	 * このメソッドは、サーバーとの接続を確立したのち、指定されたトピックで配信されたメッセージをファイルに保存する。
	 * 受信完了後はサーバーとの接続を切断する。
	 * <p>受信メッセージは、<em>msgEncoding</em> で指定された文字セットによってデコードし、
	 * <em>fileEncoding</em> で指定された文字セットでファイルに保存する。
	 * <em>msgEncoding</em> が <tt>null</tt> の場合は、バイナリデータとしてメッセージの内容を
	 * そのままファイルに出力する。
	 * <p>
	 * このメソッドでは、引数の正当性は検証しない。
	 * @param topic			トピック
	 * @param qos			伝送品質(0, 1, 2)
	 * @param msgEncoding	メッセージの文字セット名、バイナリとして保存する場合は <tt>null</tt>
	 * @param target		出力先ファイル
	 * @param fileEncoding	ファイル出力時の文字セット名、<tt>null</tt> の場合は機種依存
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean subscribeFile(String topic, int qos, String msgEncoding, File target, String fileEncoding) {
		// connection
		if (!connect()) {
			notifySubscribeStarted();
			return false;
		}
		
		// data set
		_topic = topic;
		_qos   = qos;
		_msgEncoding  = msgEncoding;
		_fileEncoding = (fileEncoding==null ? Charset.defaultCharset().name() : fileEncoding);
		_targetFile   = target;
		_recvTimeout  = 0L;
		_recvLimit    = BigDecimal.valueOf(1L);
		_strAppendLineSep = null;
		_binAppendLineSep = null;

		// subscription
		subscribeAndWait();
		reportSubscribeInfo();
		
		// disconnect
		disconnect();
		return isSucceeded();
		
	}
	
	/**
	 * 指定されたトピックを購読し、受信したメッセージをファイルに保存する。<br>
	 * このメソッドは、サーバーとの接続を確立したのち、指定されたトピックで配信されたメッセージをファイルに保存する。
	 * 受信完了後はサーバーとの接続を切断する。
	 * <p>受信メッセージは、<em>msgEncoding</em> で指定された文字セットによってデコードし、
	 * <em>fileEncoding</em> で指定された文字セットでファイルに保存する。
	 * <em>msgEncoding</em> が <tt>null</tt> の場合は、バイナリデータとしてメッセージの内容を
	 * そのままファイルに出力する。
	 * <p>このメソッドでは、<em>maxMessages</em> に指定された数だけメッセージを受信し、その内容を
	 * ファイルへ出力する。次のメッセージを受信するまでに <em>timeout</em> に指定された時間が経過した
	 * 場合は、受信を中断する。
	 * <em>appendLineSep</em> が <tt>true</tt> の場合、受信したメッセージの終端に、
	 * 機種依存の改行文字を付加する。
	 * <p>
	 * このメソッドでは、引数の正当性は検証しない。
	 * @param topic			トピック
	 * @param qos			伝送品質(0, 1, 2)
	 * @param maxMessages	最大受信メッセージ数（0 以下の場合は無制限）
	 * @param timeout		終了と判定するためのタイムアウト時間[ミリ秒]（0 以下の場合は無制限）
	 * @param appendLineSep	受信したメッセージの終端に機種依存の改行文字を付加する場合は <tt>true</tt>
	 * @param msgEncoding	メッセージの文字セット名、バイナリとして保存する場合は <tt>null</tt>
	 * @param target		出力先ファイル
	 * @param fileEncoding	ファイル出力時の文字セット名、<tt>null</tt> の場合は機種依存
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean subscribeCsvRecord(String topic, int qos, BigDecimal maxMessages, long timeout,
									boolean appendLineSep, String msgEncoding, File target, String fileEncoding)
	{
		// connection
		if (!connect()) {
			notifySubscribeStarted();
			return false;
		}
		
		// data set
		_topic = topic;
		_qos   = qos;
		_msgEncoding  = msgEncoding;
		_fileEncoding = (fileEncoding==null ? Charset.defaultCharset().name() : fileEncoding);
		_targetFile   = target;
		if (maxMessages != null && maxMessages.compareTo(BigDecimal.ZERO) > 0) {
			_recvLimit = new BigDecimal(maxMessages.toBigInteger());
		} else {
			_recvLimit = null;
		}
		if (timeout > 0L) {
			_recvTimeout = timeout;
		} else {
			_recvTimeout  = 0L;
		}
		//--- append line separator
		if (appendLineSep) {
			try {
				String strLineSep = System.getProperty("line.separator");
				_binAppendLineSep = strLineSep.getBytes(_fileEncoding);
				_strAppendLineSep = strLineSep;
			}
			catch (Throwable ex) {
				traceErrorMessage("AADLMqttFileSubscriber#subscribeCsvRecord()", ex, "Failed to get Line separator bytes for " + _fileEncoding);
				_strAppendLineSep = null;
				_binAppendLineSep = null;
			}
		} else {
			_strAppendLineSep = null;
			_binAppendLineSep = null;
		}

		// subscription
		subscribeAndWait();
		reportSubscribeInfo();
		
		// disconnect
		disconnect();
		return isSucceeded();
	}

	//------------------------------------------------------------
	// ssac.aadl.runtime.mqtt.MqttEventHandler interfaces
	//------------------------------------------------------------
	
	@Override
	public void disconnected(MqttSession session) {
		// no action
	}

	@Override
	public void connectionLost(MqttSession session, Throwable cause) {
		// サーバー喪失による中断
		requestTerminate();
		synchronized (_responseLock) {
			_responseLock.notifyAll();
		}
	}
	
	@Override
	public void messageArrived(MqttSession session, MqttArrivedMessage message) throws Exception
	{
		try {
			traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "start.");

			// check complete
			//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "will check complete.");
			synchronized (_responseLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "get responseLock : check complete.");
				_recvTime = System.currentTimeMillis();
				_recvCount = _recvCount.add(BigDecimal.ONE);
				traceArrivedMessage(message, _recvCount, _recvTime);
				if (isRequestedTerminate() || _completed) {
					_completed = true;
					traceDroppedMessage(message.getTopic(), _recvCount, _recvTime);
					_droppedCount = _droppedCount.add(BigDecimal.ONE);
					_droppedBytes = _droppedBytes.add(BigDecimal.valueOf(message.getPayload().getLength()));
					_responseLock.notifyAll();
					//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release responseLock : check complete.");
					traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "finished.");
					return;
				}
				//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release responseLock : check complete.");
			}
			
			// check stream
			//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "will check stream.");
			synchronized (_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "get streamLock : will check stream.");
				if (isBinaryMode()) {
					if (_oStream == null) {
						//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Binary model] streamLock waiting...");
						try {
							_streamLock.wait();
						} catch (InterruptedException ignoreEx) { ignoreEx=null; }
						//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Binary model] streamLock received notify.");
						if (_oStream == null) {
							String errmsg = "Output stream is not ready!";
							IllegalStateException errex = new IllegalStateException(errmsg);
							requestTerminate();
							setLastError(errmsg, errex);
							//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release streamLock : will check stream.");
							traceErrorMessage("AADLMqttFileSubscriber#messageArrived", errex, errmsg);
							throw errex;
						}
					}
				} else {
					if (_writer == null) {
						//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Text model] streamLock waiting...");
						try {
							_streamLock.wait();
						} catch (InterruptedException ignoreEx) { ignoreEx=null; }
						//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Text model] streamLock received notify.");
						if (_writer == null) {
							String errmsg = "Writer is not ready!";
							IllegalStateException errex = new IllegalStateException(errmsg);
							requestTerminate();
							setLastError(errmsg, errex);
							//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release streamLock : will check stream.");
							traceErrorMessage("AADLMqttFileSubscriber#messageArrived", errex, errmsg);
							throw errex;
						}
					}
				}
				//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release streamLock : will check stream.");
			}
			
			//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "store message.");
			synchronized (_responseLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "get responseLock : store message.");
				// check already completed or terminated
				if (isRequestedTerminate() || _completed) {
					_completed = true;
					traceDroppedMessage(message.getTopic(), _recvCount, _recvTime);
					_droppedCount = _droppedCount.add(BigDecimal.ONE);
					_droppedBytes = _droppedBytes.add(BigDecimal.valueOf(message.getPayload().getLength()));
					_responseLock.notifyAll();
					//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release responseLock : store message.");
					traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "finished.");
					return;
				}
				
				// store message
				if (isBinaryMode()) {
					synchronized (_streamLock) {
						//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Binary mode] get streamLock : store binary message.");
						try {
							storeBinaryMessage(session, message);
						}
						catch (Exception ex) {
							requestTerminate();
							String errmsg = "Failed to store binary message.";
							setLastError(errmsg, ex);
							traceErrorMessage("AADLMqttFileSubscriber#messageArrived", ex, errmsg);
							_responseLock.notifyAll();
							//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Binary mode] release streamLock : store binary message.");
							//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release responseLock : store message.");
							throw ex;
						}
						//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Binary mode] release streamLock : store binary message.");
					}
				} else {
					synchronized (_streamLock) {
						//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Text mode] get streamLock : store text message.");
						try {
							storeTextMessage(session, message);
						}
						catch (Exception ex) {
							requestTerminate();
							String errmsg = "Failed to store text message.";
							setLastError(errmsg, ex);
							traceErrorMessage("AADLMqttFileSubscriber#messageArrived", ex, errmsg);
							_responseLock.notifyAll();
							//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Text mode] release streamLock : store text message.");
							//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release responseLock : store message.");
							throw ex;
						}
						//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "[Text mode] release streamLock : store text message.");
					}
				}
				//--- accept message
				traceStoredMessage(message.getTopic(), _recvCount, _recvTime);
				_storedCount = _storedCount.add(BigDecimal.ONE);
				_storedBytes = _storedBytes.add(BigDecimal.valueOf(message.getPayload().getLength()));
				
				// check receive limit
				if (_recvLimit != null && _recvCount.compareTo(_recvLimit) >= 0) {
					_completed = true;
				}
				_responseLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "release responseLock : store message.");
			}
			traceInfoMessage("AADLMqttFileSubscriber#messageArrived", "finished.");
		}
		catch (Exception ex) {
			traceErrorMessage("AADLMqttFileSubscriber#messageArrived", ex, "Cought exception!");
			if (isDebugMode()) {
				ex.printStackTrace(System.err);
			}
			throw ex;
		}
	}
	
	protected void storeBinaryMessage(MqttSession session, MqttArrivedMessage message) throws Exception
	{
		MqPayload data = message.getPayload();
		_oStream.write(data.getData(), data.getOffset(), data.getLength());
		if (_binAppendLineSep != null) {
			_oStream.write(_binAppendLineSep);
		}
	}
	
	protected void storeTextMessage(MqttSession session, MqttArrivedMessage message) throws Exception
	{
		// output message
		MqPayload data = message.getPayload();
		_writer.write(data.toString(_msgEncoding));
		if (_strAppendLineSep != null) {
			_writer.write(_strAppendLineSep);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected String generateClientID() {
		return MqttUtil.generateClientID("aadlSub");
	}
	
	protected void traceArrivedMessage(MqttArrivedMessage message, BigDecimal msgCount, long recvTime) throws Exception
	{
		if (isDebugMode()) {
			String dbgmsg = String.format("[%s] Message arrived (#%s) : topic[%s], bytes[%d], QoS(%d), retained(%b)",
								new Timestamp(recvTime),
								msgCount.toPlainString(),
								message.getTopic(),
								message.getPayload().getLength(),
								message.getQos(),
								message.isRetained());
			System.out.println(dbgmsg);
		}
	}
	
	protected void traceStoredMessage(String topic, BigDecimal msgCount, long recvTime) {
		if (isDebugMode()) {
			String dbgmsg = String.format("[%s] Message %s (#%s) : topic[%s]",
								new Timestamp(recvTime),
								"stored",
								msgCount.toPlainString(),
								topic);
			System.out.println(dbgmsg);
		}
	}
	
	protected void traceDroppedMessage(String topic, BigDecimal msgCount, long recvTime) {
		if (isDebugMode()) {
			String dbgmsg = String.format("[%s] Message %s (#%s) : topic[%s]",
								new Timestamp(recvTime),
								"dropped",
								msgCount.toPlainString(),
								topic);
			System.out.println(dbgmsg);
		}
	}
	
	protected boolean isBinaryMode() {
		return (_msgEncoding == null);
	}
	
	@Override
	protected boolean connect() {
		// check running
		if (_responseLock != null || _streamLock != null) {
			String errmsg = "Subscriber already running!";
			IllegalStateException ex = new IllegalStateException(errmsg);
			setLastError(errmsg, ex);
			traceErrorMessage("AADLMqttFileSubscriber#connect", null, errmsg);
			return false;
		}
		resetTerminateRequest();
		
		// connection
		return super.connect();
	}
	
	protected void reportSubscribeInfo() {
		if (isVerbose()) {
			String msg = String.format("...subscribed <stored> %s messages, %s bytes  <dropped> %s messages, %s bytes",
								(_storedCount==null ? "0" : _storedCount.toPlainString()),
								(_storedBytes==null ? "0" : _storedBytes.toPlainString()),
								(_droppedCount==null ? "0" : _droppedCount.toPlainString()),
								(_droppedBytes==null ? "0" : _droppedBytes.toPlainString()));
			System.out.println(msg);
		}
	}
	
	protected boolean subscribeAndWait() {
		traceInfoMessage("AADLMqttFileSubscriber#subscribeAndWait", "start.");
		// initialize
		_responseLock = new Object();
		_streamLock   = new Object();
		_wasTimeout   = false;
		_completed    = false;
		_oStream = null;
		_writer  = null;
		_recvCount = BigDecimal.ZERO;
		_recvTime  = System.currentTimeMillis();
		
		_storedCount = BigDecimal.ZERO;
		_storedBytes = BigDecimal.ZERO;
		_droppedCount = BigDecimal.ZERO;
		_droppedBytes = BigDecimal.ZERO;

		// subscribe
		try {
			//traceInfoMessage("AADLMqttFileSubscriber#subscribeAndWait", "subscribe...");
			synchronized (_responseLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#subscribeAndWait", "get responseLock : subscribe...");
				_mqttSession.subscribe(_topic, _qos);
				_recvTime  = System.currentTimeMillis();
				if (isVerbose()) {
					System.out.println("Subscribe [" + String.valueOf(_topic) + "], QoS(" + _qos + ")");
				}
				//traceInfoMessage("AADLMqttFileSubscriber#subscribeAndWait", "release responseLock : subscribe...");
			}
		}
		catch (Throwable ex) {
			// failed to subscribe
			String errmsg = "Failed to subscribe[" + String.valueOf(_topic) + "], QoS(" + _qos + ")";
			setLastError(errmsg, ex);
			traceErrorMessage("AADLMqttFileSubscriber#subscribeAndWait", getLastErrorCause(), errmsg);
			//traceInfoMessage("AADLMqttFileSubscriber#subscribeAndWait", "release responseLock : subscribe...");
			return false;
		}
		finally {
			notifySubscribeStarted();
		}
		
		// wait all messages
		boolean result;
		if (isBinaryMode()) {
			// binary mode
			result = binaryWaitAllMessages();
		} else {
			// text mode
			result = textWaitAllMessages();
		}
		if (isError()) {
			traceErrorMessage("AADLMqttFileSubscriber#subscribeAndWait", getLastErrorCause(), getLastErrorMessage());
		}
		
		//traceInfoMessage("AADLMqttFileSubscriber#subscribeAndWait", "will unsubscribe.");
		// unsubscribe
		try {
			_mqttSession.unsubscribe(_topic);
		}
		catch (Throwable ex) {
			if (result) {
				String errmsg = "Failed to unsubscribe[" + String.valueOf(_topic) + "]";
				setLastError(errmsg, ex);
				traceErrorMessage("AADLMqttFileSubscriber#subscribeAndWait", getLastErrorCause(), errmsg);
				return false;
			}
		}
		if (isVerbose()) {
			System.out.println("Unsubscribe [" + String.valueOf(_topic) + "]");
		}
		
		// completed
		traceFormatInfoMessage("AADLMqttFileSubscriber#subscribeAndWait", "finished(%b)", result);
		return result;
	}
	
	protected boolean waitAllMessages() {
		traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "start.");
		synchronized (_responseLock) {
			//traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "get responseLock : wait...");
			boolean terminated = isRequestedTerminate();
			boolean completed = _completed;
			while (!completed && !terminated) {
				if (_recvTimeout > 0L) {
					long remainTime = _recvTimeout - (System.currentTimeMillis() - _recvTime);
					if (remainTime > 0) {
						//traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "start waiting...");
						try {
							_responseLock.wait(remainTime);
						} catch (InterruptedException ignoreEx) { ignoreEx=null; }
						//traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "waiting finished.");
					}
					if (!_completed) {
						if ((System.currentTimeMillis() - _recvTime) >= _recvTimeout) {
							_wasTimeout = true;
							_completed = true;
						}
					}
				} else {
					//traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "start waiting...");
					try {
						_responseLock.wait();
					} catch (InterruptedException ignoreEx) { ignoreEx=null; }
					//traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "waiting finished.");
				}
				terminated = isRequestedTerminate();
				completed  = _completed;
			}
			//traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "release responseLock : wait...");
		}
		
		// check errors
		if (_mqttSession.isConnectionLost()) {
			// connection lost
			Throwable cause = _mqttSession.getConnectionLostCause();
			if (cause != null) {
				setLastError("Connection lost : " + cause.toString(), cause);
			} else {
				setLastError("Connection lost!", null);
			}
			traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "finished(false) : mqttSession error");
			return false;
		}
		else if (!_completed) {
			// terminated
			setLastError(MSG_TERMINATED_SUBSCRIPTION, null);
			traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "finished(false) : not completed!");
			return false;
		}
		else if (_wasTimeout) {
			// succeeded by timeout
			traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "finished(true) : was timeout.");
			if (isVerbose()) {
				System.out.println(MSG_TIMEOUT_SUBSCRIPTION);
			}
			return true;
		}
		else {
			// succeeded
			traceInfoMessage("AADLMqttFileSubscriber#waitAllMessages", "finished(true) : succeeded.");
			return true;
		}
	}
	
	protected boolean binaryWaitAllMessages() {
		boolean result = false;
		// binary mode
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "start.");
		
		try {
			fos = new FileOutputStream(_targetFile);
			bos = new BufferedOutputStream(fos);
			
			// set stream
			//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "set stream.");
			synchronized(_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "get streamLock : set stream.");
				_oStream = bos;
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "release streamLock : set stream.");
			}
			
			// wait messages
			result = waitAllMessages();
			
			// unset stream
			//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "unset stream.");
			synchronized(_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "get streamLock : unset stream.");
				_oStream = null;
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "release streamLock : unset stream.");
			}
		}
		catch (FileNotFoundException ex) {
			String errmsg = "Could not open file : " + _targetFile.getAbsolutePath();
			setLastError(errmsg, ex);
			result = false;
			traceErrorMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", getLastErrorCause(), errmsg);
			synchronized(_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "get streamLock : FileNotFoundException.");
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "release streamLock : FileNotFoundException.");
			}
		}
		finally {
			traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "close stream.");
			synchronized(_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "get streamLock : close stream.");
				if (bos != null) {
					closeStream(bos);
					bos = null;
				}
				if (fos != null) {
					closeStream(fos);
					fos = null;
				}
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "release streamLock : close stream.");
			}
			traceFormatInfoMessage("AADLMqttFileSubscriber#binaryWaitAllMessages", "finished(%b)", result);
		}
		return result;
	}
	
	protected boolean textWaitAllMessages() {
		boolean result = false;
		// text mode
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		
		traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "start.");
		
		try {
			fos = new FileOutputStream(_targetFile);
			osw = new OutputStreamWriter(fos, _fileEncoding);
			bw = new BufferedWriter(osw);
			
			// set stream
			//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "set stream.");
			synchronized(_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "get streamLock : set stream.");
				_writer = bw;
				_oStream = fos;
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "release streamLock : set stream.");
			}
			
			// wait messages
			result = waitAllMessages();
			
			// unset stream
			//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "unset stream.");
			synchronized(_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "get streamLock : unset stream.");
				_writer = null;
				_oStream = null;
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "release streamLock : unset stream.");
			}
		}
		catch (FileNotFoundException ex) {
			String errmsg = "Could not open file : " + _targetFile.getAbsolutePath();
			setLastError(errmsg, ex);
			result = false;
			traceErrorMessage("AADLMqttFileSubscriber#textWaitAllMessages", getLastErrorCause(), errmsg);
			synchronized(_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "get streamLock : FileNotFoundException.");
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "release streamLock : FileNotFoundException.");
			}
		} catch (UnsupportedEncodingException ex) {
			String errmsg = "Unsupported file encoding : " + String.valueOf(_fileEncoding);
			setLastError(errmsg, ex);
			result = false;
			traceErrorMessage("AADLMqttFileSubscriber#textWaitAllMessages", getLastErrorCause(), errmsg);
			synchronized(_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "get streamLock : UnsupportedEncodingException.");
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "release streamLock : UnsupportedEncodingException.");
			}
		}
		finally {
			traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "close stream.");
			synchronized (_streamLock) {
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "get streamLock : close stream.");
				if (bw != null) {
					closeStream(bw);
					bw = null;
				}
				if (osw != null) {
					closeStream(osw);
					osw = null;
				}
				if (fos != null) {
					closeStream(fos);
					fos = null;
				}
				_streamLock.notifyAll();
				//traceInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "release streamLock : close stream.");
			}
			traceFormatInfoMessage("AADLMqttFileSubscriber#textWaitAllMessages", "finished(%b)", result);
		}
		return result;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
