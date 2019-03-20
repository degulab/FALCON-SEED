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
 * @(#)MqttException.java	0.3.0	2013/06/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttException.java	0.2.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

/**
 * MQTT ランタイムライブラリにおける例外。
 * 
 * @version 0.3.0	2013/06/27
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttException extends Exception
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -8233009461866512351L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 詳細メッセージに <tt>null</tt> を使用して、新規例外を構築する。
	 */
	public MqttException() {
		super();
	}

	/**
	 * 指定された詳細メッセージを使用して、新規例外を構築する。
	 * @param message	詳細メッセージ
	 */
	public MqttException(String message) {
		super(message);
	}

	/**
	 * 指定された原因と詳細メッセージ <code>(cause==null ? null : cause.toString())</code> を持つ、新規例外を構築する。
	 * @param cause	原因
	 */
	public MqttException(Throwable cause) {
		super(cause);
	}

	/**
	 * 指定された詳細メッセージおよび原因を使用して、新規例外を構築する。
	 * <em>cause</em> と関連付けられた詳細メッセージが、この実行時例外の詳細メッセージに自動的に統合されることはない。
	 * @param message	詳細メッセージ
	 * @param cause		原因
	 */
	public MqttException(String message, Throwable cause) {
		super(message, cause);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
