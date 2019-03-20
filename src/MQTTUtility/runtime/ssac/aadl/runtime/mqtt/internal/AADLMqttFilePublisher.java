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
 * @(#)AADLMqttFilePublisher.java	0.3.1	2013/07/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMqttFilePublisher.java	0.3.0	2013/06/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMqttFilePublisher.java	0.2.0	2013/04/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMqttFilePublisher.java	0.1.0	2013/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import ssac.aadl.runtime.mqtt.MqDeliveryToken;
import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.MqttSession;
import ssac.aadl.runtime.mqtt.MqttTimedOutException;
import ssac.aadl.runtime.mqtt.MqttUtil;
import ssac.aadl.runtime.nio.csv.CsvFileTokenizer;

/**
 * AADL 専用の ファイル Publisher クラス。
 * 
 * @version 0.3.1	2013/07/05
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class AADLMqttFilePublisher extends AADLMqttClient
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String MSG_TERMINATED_PUBLISH = "Publish was terminated!";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected boolean		_running;
	
	protected String		_topic;
	protected int			_qos;
	protected boolean		_retained;
	protected String		_msgEncoding;
	protected String		_fileEncoding;
	protected Charset		_csMessage;
	protected Charset		_csFile;
	protected File			_targetFile;
	protected BigDecimal	_skipRecords;
	
	protected long			_sendInterval;
	protected boolean		_appendRecordSep;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの接続パラメータで、新しいインスタンスを生成する。
	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 */
	public AADLMqttFilePublisher() {
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
	public AADLMqttFilePublisher(String serverURI) {
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
	public AADLMqttFilePublisher(String serverURI, int portNo) {
		super(serverURI, portNo, generateClientID());
	}

	/**
	 * 指定された接続パラメータで、新しいインスタンスを生成する。
	 * @param params	接続パラメータ
	 * @throws NullPointerException	<em>params</em> が <tt>null</tt> の場合
	 */
	public AADLMqttFilePublisher(MqttConnectionParams params) {
		super(params);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたファイルを指定されたトピックに対し、単一のメッセージとして送信する。<br>
	 * このメソッドは、サーバーとの接続を確立したのち、指定されたファイルをメッセージとして送信する。
	 * 送信完了後はサーバーとの接続を切断する。
	 * <p>ファイル送信時には、<em>fileEncoding</em> で指定された文字セットによってデコードし、
	 * <em>msgEncoding</em> で指定された文字セットでエンコードしたバイトデータを送信する。
	 * <em>msgEncoding</em> が <tt>null</tt> の場合は、バイナリデータとしてファイルの内容を
	 * そのまま送信する。
	 * <p>
	 * このメソッドでは、引数の正当性は検証しない。
	 * @param topic			トピック
	 * @param qos			伝送品質(0, 1, 2)
	 * @param retained		サーバーにメッセージを残す場合は <tt>true</tt>
	 * @param msgEncoding	メッセージの文字セット名、バイナリとして送信する場合は <tt>null</tt>
	 * @param target		送信するファイル
	 * @param fileEncoding	送信するファイルを読み込む際の文字セット名、<tt>null</tt> の場合は機種依存
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean publishFile(String topic, int qos, boolean retained, String msgEncoding, File target, String fileEncoding) {
		// check running
		if (checkAlreadyRunning())
			return false;
		_running = true;
		
		// data set
		if (!setupData(topic, qos, retained, msgEncoding, fileEncoding)) {
			_running = false;
			return false;
		}
		_targetFile   = target;
		_sendInterval = 0L;
		_appendRecordSep = false;
		
		// connection
		if (!connect()) {
			_running = false;
			return false;
		}
		
		// publish
		publishOneMessage();
		
		// disconnect
		disconnect();
		_running = false;
		return isSucceeded();
	}
	
	/**
	 * 指定されたCSVファイルを指定されたトピックに対し、レコードごとに送信する。<br>
	 * このメソッドは、サーバーとの接続を確立したのち、指定されたファイルをメッセージとして送信する。
	 * 送信完了後はサーバーとの接続を切断する。
	 * <p>ファイル送信時には、<em>fileEncoding</em> で指定された文字セットによってデコードし、
	 * <em>msgEncoding</em> で指定された文字セットでエンコードしたバイトデータを送信する。
	 * <em>msgEncoding</em> が <tt>null</tt> の場合は、<em>fileEncoding</em> と
	 * 同じ文字セットでエンコードされる。
	 * <p>各レコードは <em>msgInterval</em> で指定された送信間隔で送信される。
	 * また、<em>appendRecordSep</em> が <tt>false</tt> の場合、レコード終端の
	 * レコード区切り文字は除外される。
	 * <p>
	 * このメソッドでは、引数の正当性は検証しない。
	 * @param topic			トピック
	 * @param qos			伝送品質(0, 1, 2)
	 * @param retained		サーバーにメッセージを残す場合は <tt>true</tt>
	 * @param skipRecords	先頭から除外するレコード数
	 * @param msgInterval	次のメッセージを送信するまでの時間間隔[ミリ秒]、0 以下の場合は即時送信
	 * @param appendRecordSep	レコード区切り文字を含める場合は <tt>true</tt>
	 * @param msgEncoding	メッセージの文字セット名
	 * @param target		送信するファイル
	 * @param fileEncoding	送信するファイルを読み込む際の文字セット名、<tt>null</tt> の場合は機種依存
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean publishCsvFile(String topic, int qos, boolean retained, BigDecimal skipRecords, long msgInterval, boolean appendRecordSep,
									String msgEncoding, File target, String fileEncoding)
	{
		// check running
		if (checkAlreadyRunning())
			return false;
		_running = true;
		
		// data set
		if (!setupData(topic, qos, retained, msgEncoding, fileEncoding)) {
			_running = false;
			return false;
		}
		_targetFile   = target;
		if (msgInterval > 0L) {
			_sendInterval = msgInterval;
		} else {
			_sendInterval = 0L;
		}
		_appendRecordSep = appendRecordSep;
		if (skipRecords == null || skipRecords.compareTo(BigDecimal.ZERO) <= 0) {
			_skipRecords = BigDecimal.ZERO;
		} else {
			_skipRecords = new BigDecimal(skipRecords.toBigInteger());
		}
		
		// connection
		if (!connect()) {
			_running = false;
			return false;
		}
		
		// publish
		publishCsvRecords();
		
		// disconnect
		disconnect();
		_running = false;
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
	}
	
	@Override
	public void messageArrived(MqttSession session, MqttArrivedMessage message) throws Exception
	{
		// no action
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	static protected String generateClientID() {
		return MqttUtil.generateClientID("aadlPub");
	}
	
	protected boolean checkAlreadyRunning() {
		if (_running) {
			String errmsg = "Publisher already running!";
			IllegalStateException ex = new IllegalStateException(errmsg);
			setLastError(errmsg, ex);
			traceErrorMessage("AADLMqttFilePublisher#checkAlreadyRunning", null, errmsg);
			return true;
		}
		return false;
	}
	
	protected boolean isBinaryMode() {
		return (_msgEncoding == null);
	}
	
	protected boolean setupData(String topic, int qos, boolean retained, String msgEncoding, String fileEncoding) {
		_topic    = topic;
		_qos      = qos;
		_retained = retained;
		
		// message encoding
		try {
			if (msgEncoding != null && msgEncoding.length() > 0) {
				_msgEncoding = msgEncoding;
				_csMessage = Charset.forName(msgEncoding);
			} else {
				_msgEncoding = null;
				_csMessage = null;
			}
		}
		catch (Throwable ex) {
			setLastError("Unsupported message encoding : " + String.valueOf(msgEncoding), ex);
			return false;
		}
		
		// file encoding
		try {
			if (fileEncoding != null && fileEncoding.length() > 0) {
				_fileEncoding = fileEncoding;
				_csFile = Charset.forName(fileEncoding);
			} else {
				_csFile = Charset.defaultCharset();
				_fileEncoding = _csFile.name();
			}
		}
		catch (Throwable ex) {
			setLastError("Unsupported file encoding : " + String.valueOf(fileEncoding), ex);
			return false;
		}
		
		return true;
	}
	
	protected boolean publishOneMessage() {
		// read data
		byte[] data = null;
		if (isBinaryMode()) {
			// binary mode
			data = readBinaryFile();
		}
		else {
			// text mode
			data = readTextFile(_csMessage, _csFile);
		}
		if (data == null) {
			return false;
		}
		
		// check terminate
		if (isRequestedTerminate()) {
			setLastError(MSG_TERMINATED_PUBLISH, null);
			return false;
		}
		
		// publish
		if (isVerbose()) {
			System.out.println("Publish [" + String.valueOf(_topic) + "], " + data.length + " bytes, QoS(" + _qos + "), retained(" + _retained + ")...");
		}
		boolean result = publishAndWait(_topic, data, _qos, _retained);
		if (result) {
			System.out.println("Publish done.");
		} else {
			System.out.println("Publish failed!");
		}
		return result;
	}
	
	protected void reportPublishInfo(BigDecimal totalSkipRecords, long totalRecords, long totalBytes) {
		if (isVerbose()) {
			if (totalSkipRecords.compareTo(BigDecimal.ZERO) > 0) {
				System.out.println("...skipped " + totalSkipRecords.toPlainString() + " records.");
			}
			System.out.println("...published " + totalRecords + " records, " + totalBytes + " bytes.");
		}
	}
	
	protected boolean publishCsvRecords() {
		boolean isBinary = isBinaryMode();
		boolean result = false;
		
		// open file
		CsvFileTokenizer csvTokenizer = null;
		try {
			csvTokenizer = new CsvFileTokenizer(_targetFile, _csFile, 8192*2);
			csvTokenizer.setRecordBytesStored(isBinary);
			//--- 改行文字を含めるか
			csvTokenizer.setRecordDelimiterStored(_appendRecordSep);
			
			result = true;
		}
		catch (FileNotFoundException ex) {
			String errmsg = "File not found : " + _targetFile.getAbsolutePath();
			setLastError(errmsg, ex);
			result = false;
		}
		catch (Throwable ex) {
			String errmsg = "Failed to access file : " + _targetFile.getAbsolutePath();
			setLastError(errmsg, ex);
			result = false;
		}
		if (!result) {
			if (csvTokenizer != null) {
				try {
					csvTokenizer.close();
				} catch (Throwable ignoreEx) { ignoreEx=null; }
			}
			return false;
		}
		
		if (isVerbose()) {
			System.out.println("Start publish [" + String.valueOf(_topic) + "], QoS(" + _qos + "), retained(" + _retained + ")...");
		}

		// publish
		BigDecimal totalSkipRecords = BigDecimal.ZERO;
		long totalSentRecords = 0L;
		long totalSentBytes   = 0L;
		try {
			// skip records
			String[] csvFields = csvTokenizer.nextRecord();
			if (csvFields != null && _skipRecords != null && _skipRecords.compareTo(BigDecimal.ZERO) > 0) {
				for (; csvFields != null; ) {
					//--- read next record
					csvFields = csvTokenizer.nextRecord();
					//--- increment skip record count
					totalSkipRecords = totalSkipRecords.add(BigDecimal.ONE);
					//--- check complete to skip
					if (totalSkipRecords.compareTo(_skipRecords) >= 0) {
						// skip finished
						break;
					}
				}
			}
			
			// publish remain records
			for (; csvFields != null; ) {
				// publish
				byte[] message;
				if (isBinary) {
					message = csvTokenizer.getRecordBytes();
				} else {
					message = csvTokenizer.getRecordString().getBytes(_csMessage);
				}
				if (!publishAndWait(_topic, message, _qos, _retained)) {
					// error
					reportPublishInfo(totalSkipRecords, totalSentRecords, totalSentBytes);
					System.out.println("Publish failed!");
					return false;
				}
				totalSentRecords++;
				totalSentBytes += message.length;
				long sentTime = System.currentTimeMillis();
				
				// 次のレコードを読み込む
				csvFields = csvTokenizer.nextRecord();
				if (csvFields != null) {
					// check terminate
					if (isRequestedTerminate()) {
						// terminate
						setLastError(MSG_TERMINATED_PUBLISH, null);
						reportPublishInfo(totalSkipRecords, totalSentRecords, totalSentBytes);
						return false;
					}
					
					// 指定時間待機
					if (_sendInterval > 0) {
						long remainTime = _sendInterval - (System.currentTimeMillis() - sentTime);
						if (remainTime > 0) {
							try {
								Thread.sleep(remainTime);
							} catch (Throwable ignoreEx) { ignoreEx=null; }
						}
					}
					
				}
			}
			reportPublishInfo(totalSkipRecords, totalSentRecords, totalSentBytes);
			System.out.println("Publish done.");
			return true;
		}
		catch (IOException ex) {
			String errmsg = "Failed to read file : " + _targetFile.getAbsolutePath();
			setLastError(errmsg, ex);
			reportPublishInfo(totalSkipRecords, totalSentRecords, totalSentBytes);
			System.out.println("Publish failed!");
			return false;
		}
		catch (Throwable ex) {
			String errmsg = "Failed to publish[" + String.valueOf(_topic) + "], QoS(" + _qos + "), retained(" + _retained + ")";
			setLastError(errmsg, ex);
			reportPublishInfo(totalSkipRecords, totalSentRecords, totalSentBytes);
			System.out.println("Publish failed!");
			return false;
		}
		finally {
			if (csvTokenizer != null) {
				try {
					csvTokenizer.close();
				} catch (Throwable ignoreEx) { ignoreEx=null; }
			}
		}
	}
	
	protected boolean publishAndWait(String topic, byte[] message, int qos, boolean retaind) {
		MqDeliveryToken token = null;
		try {
			token = _mqttSession.asyncPublish(_topic, message, _qos, _retained);
		}
		catch (Throwable ex) {
			setLastError("Failed to publish to [" + String.valueOf(_topic) + "]", ex);
			return false;
		}
		
		// wait
		try {
			while (!token.isCompleted() && !isRequestedTerminate()) {
				try {
					token.waitForCompletion(5000);	// 5秒待つ
				}
				catch (MqttTimedOutException ignoreEx) {
					// タイムアウト例外は無視
					ignoreEx = null;
				}
			}
		}
		catch (Throwable ex) {
			setLastError("Failed to publish to [" + String.valueOf(_topic) + "]", ex);
			return false;
		}
		if (token.isCompleted()) {
			return true;
		}
		else {
			// terminate
			setLastError(MSG_TERMINATED_PUBLISH, null);
			return false;
		}
	}
	
	protected byte[] readBinaryFile() {
		byte[] result = null;
		FileInputStream fis = null;
		
		// check file size
		if (_targetFile.length() > (long)Integer.MAX_VALUE) {
			String errmsg = "Target file size is too large : " + _targetFile.getAbsolutePath();
			IllegalStateException ex = new IllegalStateException(errmsg);
			setLastError(errmsg, ex);
			return null;
		}

		// read file
		try {
			int remainSize = (int)_targetFile.length();
			result = new byte[remainSize];
			fis = new FileInputStream(_targetFile);
			fis.read(result);
		}
		catch (FileNotFoundException ex) {
			String errmsg = "File not found : " + _targetFile.getAbsolutePath();
			setLastError(errmsg, ex);
			result = null;
		}
		catch (Throwable ex) {
			String errmsg = "Failed to read file : " + _targetFile.getAbsolutePath();
			setLastError(errmsg, ex);
			result = null;
		}
		finally {
			if (fis != null) {
				closeStream(fis);
				fis = null;
			}
		}
		return result;
	}
	
	protected byte[] readTextFile(Charset csMessage, Charset csFile) {
		byte[] result = readBinaryFile();
		if (result != null && result.length > 0) {
			// convert encoding
			try {
				String str = new String(result, csFile);
				result = null;
				result = str.getBytes(csMessage);
			}
			catch (Throwable ex) {
				String errmsg = "Failed to convert encoding.";
				setLastError(errmsg, ex);
				result = null;
			}
		}
		return result;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
