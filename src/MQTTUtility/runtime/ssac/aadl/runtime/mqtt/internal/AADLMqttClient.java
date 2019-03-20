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
 * @(#)AADLMqttClient.java	0.3.1	2013/07/04
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMqttClient.java	0.2.0	2013/04/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMqttClient.java	0.1.0	2013/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.event.MqttEventHandler;

/**
 * AADL 専用 MQTT クライアントの抽象クラス。
 * 
 * @version 0.3.1	2013/07/04
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public abstract class AADLMqttClient implements MqttEventHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	volatile private boolean	_reqTerminate = false;
	volatile private boolean	__verbose = false;
	volatile private boolean	__debug = false;
	
	protected final MqttConnectionParams	_mqttParams;
	protected MqttManagedSessionImpl	_mqttSession = null;
	
	protected String	_lastErrMessage;
	protected Throwable	_lastErrCause;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された接続パラメータで、新しいインスタンスを生成する。
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
	public AADLMqttClient(String serverURI, int portNo, String clientID) {
		this(new MqttConnectionParams(serverURI, portNo, clientID));
	}

	/**
	 * 指定された接続パラメータで、新しいインスタンスを生成する。
	 * @param params	接続パラメータ
	 * @throws NullPointerException	<em>params</em> が <tt>null</tt> の場合
	 */
	public AADLMqttClient(MqttConnectionParams params) {
		if (params == null)
			throw new NullPointerException("MqttConnectionParams object is null.");
		_mqttParams = params;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getServerURI() {
		return _mqttParams.getServerURI();
	}
	
	public String getClientID() {
		return _mqttParams.getClientID();
	}
	
	public boolean isRequestedTerminate() {
		return _reqTerminate;
	}
	
	public void requestTerminate() {
		_reqTerminate = true;
	}
	
	public boolean isSucceeded() {
		return (!isError());
	}
	
	public boolean isError() {
		return (_lastErrMessage!=null && _lastErrCause!=null);
	}
	
	public String getLastErrorMessage() {
		return _lastErrMessage;
	}
	
	public Throwable getLastErrorCause() {
		return _lastErrCause;
	}
	
	public boolean isVerbose() {
		return (__debug || __verbose);
	}
	
	public boolean isVerboseMode() {
		return __verbose;
	}
	
	public void setVerboseMode(boolean toVerbose) {
		__verbose = toVerbose;
	}
	
	public boolean isDebugMode() {
		return __debug;
	}
	
	public void setDebugMode(boolean toDebug) {
		__debug = toDebug;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void resetTerminateRequest() {
		_reqTerminate = false;
	}
	
	protected boolean connect() {
		boolean result;
		if (isVerbose()) {
			System.out.print("Connect to server[" + _mqttParams.getServerURI() + "]...");
			System.out.flush();
		}
		clearLastError();
		if (_mqttSession != null) {
			if (_mqttSession.isConnected()) {
				result = true;
				traceInfoMessage("AADLMqttClient#connect", "Already connected!");
			} else {
				try {
					_mqttSession.connect();
					result = true;
				}
				catch (Throwable ex) {
					result = false;
					setLastError(ex.getMessage(), ex);
				}
			}
		}
		else {
			try {
				_mqttSession = new MqttManagedSessionImpl(_mqttParams);
				_mqttSession.setEventHandler(this);
				_mqttSession.connect();
				result = true;
			}
			catch (Throwable ex) {
				result = false;
				setLastError(ex.getMessage(), ex);
			}
		}
		if (isVerbose()) {
			if (isError()) {
				System.out.println("failed!");
			} else {
				System.out.println("done.");
			}
		}
		return result;
	}
	
	protected void disconnect() {
		if (_mqttSession != null) {
			if (_mqttSession.isConnected()) {
				if (isVerbose()) {
					System.out.println("Disconnect server[" + _mqttParams.getServerURI() + "]");
				}
				try {
					_mqttSession.disconnect();
				}
				catch (Throwable ex) {
					setLastError(ex.getMessage(), ex);
					traceErrorMessage("AADLMqttClient#disconnect", ex, "Failed to disconnect.");
				}
			}
			else if (isVerbose() && _mqttSession.isConnectionLost()) {
				System.err.println("Lost server[" + _mqttParams.getServerURI() + "]");
			}
		}
	}
	
	protected boolean hasError() {
		return (_lastErrMessage!=null || _lastErrCause!=null);
	}
	
	protected void clearLastError() {
		_lastErrMessage = null;
		_lastErrCause   = null;
	}
	
	protected void setLastError(String message, Throwable cause) {
		_lastErrMessage = message;
		_lastErrCause   = cause;
	}
	
	protected void closeStream(InputStream iStream) {
		if (iStream != null) {
			try {
				iStream.close();
			} catch (Throwable ignoreEx) { ignoreEx=null; }
		}
	}
	
	protected void closeStream(OutputStream oStream) {
		if (oStream != null) {
			try {
				oStream.close();
			} catch (Throwable ignoreEx) { ignoreEx=null; }
		}
	}
	
	protected void closeStream(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (Throwable ignoreEx) { ignoreEx=null; }
		}
	}
	
	protected void closeStream(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (Throwable ignoreEx) { ignoreEx=null; }
		}
	}
	
	protected void traceInfoMessage(String method, String message) {
		if (__debug) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Info (");
			sb.append(method);
			sb.append(")] ");
			if (message != null) {
				sb.append(message);
			}
			System.out.println(sb.toString());
		}
	}
	
	protected void traceFormatInfoMessage(String method, String format, Object...args) {
		if (__debug) {
			if (format != null) {
				traceInfoMessage(method, String.format(format, args));
			} else {
				traceInfoMessage(method, format);
			}
		}
	}
	
	protected void traceErrorMessage(String method, Throwable ex, String message) {
		if (__debug) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Error (");
			sb.append(method);
			sb.append(")] ");
			if (message != null) {
				sb.append(message);
			}
			if (ex != null) {
				sb.append("\n(cause) ");
				sb.append(ex);
			}
			System.err.println(sb.toString());
		}
	}
	
	protected void traceFormatErrorMessage(String method, Throwable ex, String format, Object...args) {
		if (__debug) {
			if (format != null) {
				traceErrorMessage(method, ex, String.format(format, args));
			} else {
				traceErrorMessage(method, ex, format);
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
