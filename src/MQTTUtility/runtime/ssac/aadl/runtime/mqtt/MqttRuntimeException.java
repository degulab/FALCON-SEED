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
 * @(#)MqttRuntimeException.java	0.2.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

/**
 * MQTT ランタイムライブラリにおける実行時例外。
 * 
 * @version 0.2.0	2013/05/15
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttRuntimeException extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -438130667174832591L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String	_mqttServerURI;
	private final String	_mqttClientID;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 詳細メッセージに <tt>null</tt> を使用して、新規例外を構築する。
	 */
	public MqttRuntimeException() {
		this((MqttConnectionParams)null);
	}

	/**
	 * 指定された詳細メッセージを使用して、新規例外を構築する。
	 * @param message	詳細メッセージ
	 */
	public MqttRuntimeException(String message) {
		this(message, (MqttConnectionParams)null);
	}

	/**
	 * 指定された原因と詳細メッセージ <code>(cause==null ? null : cause.toString())</code> を持つ、新規例外を構築する。
	 * @param cause	原因
	 */
	public MqttRuntimeException(Throwable cause) {
		this(cause, (MqttConnectionParams)null);
	}

	/**
	 * 指定された詳細メッセージおよび原因を使用して、新規例外を構築する。
	 * <em>cause</em> と関連付けられた詳細メッセージが、この実行時例外の詳細メッセージに自動的に統合されることはない。
	 * @param message	詳細メッセージ
	 * @param cause		原因
	 */
	public MqttRuntimeException(String message, Throwable cause) {
		this(message, cause, null);
	}

	/**
	 * 指定された <code>MQTT</code> 接続情報を持つ、新規例外を構築する。
	 * @param params	<code>MQTT</code> 接続情報
	 */
	public MqttRuntimeException(MqttConnectionParams params) {
		super();
		_mqttServerURI = extractServerURI(params);
		_mqttClientID  = extractClientID(params);
	}

	/**
	 * 指定された <code>MQTT</code> 接続情報と詳細メッセージを持つ、新規例外を構築する。
	 * @param message	詳細メッセージ
	 * @param params	<code>MQTT</code> 接続情報
	 */
	public MqttRuntimeException(String message, MqttConnectionParams params) {
		super(message);
		_mqttServerURI = extractServerURI(params);
		_mqttClientID  = extractClientID(params);
	}

	/**
	 * 指定された <code>MQTT</code> 接続情報と原因を持つ、新規例外を構築する。
	 * @param cause		原因	
	 * @param params	<code>MQTT</code> 接続情報
	 */
	public MqttRuntimeException(Throwable cause, MqttConnectionParams params) {
		super(cause);
		_mqttServerURI = extractServerURI(params);
		_mqttClientID  = extractClientID(params);
	}

	/**
	 * 指定された <code>MQTT</code> 接続情報、詳細メッセージおよび原因を持つ、新規例外を構築する。
	 * <em>cause</em> と関連付けられた詳細メッセージが、この実行時例外の詳細メッセージに自動的に統合されることはない。
	 * @param message	詳細メッセージ
	 * @param cause		原因
	 * @param params	<code>MQTT</code> 接続情報
	 */
	public MqttRuntimeException(String message, Throwable cause, MqttConnectionParams params) {
		super(message, cause);
		_mqttServerURI = extractServerURI(params);
		_mqttClientID  = extractClientID(params);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * この例外に設定された <code>MQTT</code> 接続情報の接続先アドレスを返す。
	 * @return	接続情報があればその接続先アドレス、なければ <tt>null</tt>
	 */
	public String getServerURI() {
		return _mqttServerURI;
	}

	/**
	 * この例外に設定された <code>MQTT</code> 接続情報のクライアントIDを返す。
	 * @return	接続情報があればそのクライアントID、なければ <tt>null</tt>
	 */
	public String getClientID() {
		return _mqttClientID;
	}

	/**
	 * この例外の詳細メッセージ文字列を返す。
	 * <code>MQTT</code> 接続情報が設定されている場合、その接続情報も詳細メッセージに付加される。
	 */
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		String orgmsg = super.getMessage();
		if ((orgmsg == null || orgmsg.length() <= 0) && getCause() instanceof MqttException) {
			orgmsg = getCause().getMessage();
		}
		if (orgmsg != null && orgmsg.length() > 0) {
			sb.append(orgmsg);
		}

		boolean existServerURI = false;
		if (_mqttServerURI != null && _mqttServerURI.length() > 0) {
			existServerURI = true;
			if (sb.length() > 0)
				sb.append(" : ");
			sb.append("serverURI=");
			sb.append(_mqttServerURI);
		}
		
		if (_mqttClientID != null && _mqttClientID.length() > 0) {
			if (sb.length() > 0) {
				sb.append(existServerURI ? ", " : " : ");
			}
			sb.append("clientID=");
			sb.append(_mqttClientID);
		}
		
		return (sb.length() > 0 ? sb.toString() : null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	static protected String extractServerURI(final MqttConnectionParams params) {
		String str = (params==null ? null : params.getServerURI());
		if (str != null && str.length() <= 0) {
			str = null;
		}
		return str;
	}
	
	static protected String extractClientID(final MqttConnectionParams params) {
		String str = (params==null ? null : params.getClientID());
		if (str != null && str.length() <= 0) {
			str = null;
		}
		return str;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
