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
 * @(#)MqttPublisher.java	0.2.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * MQTT セッションに対し、パブリッシュするインタフェース群。
 * 
 * @version 0.2.0	2013/05/15
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttPublisher extends MqttUtil
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
	
	protected MqttPublisher() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたメッセージをプラットフォーム標準文字コードのバイトデータとしてパブリッシュする。
	 * <p>このメソッドでは、サーバーにメッセージを残さず、メッセージ伝達品質に 1 を指定してパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param message		送信するメッセージ
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>message</em> のどれかが <tt>null</tt> の場合
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishString(MqttSession session, String topic, String message) {
		publishString(session, topic, message, null, 1, false);
	}
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたメッセージをプラットフォーム標準文字コードのバイトデータとしてパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param message		送信するメッセージ
	 * @param qos			メッセージ伝達品質（0, 1, 2）
	 * @param retained		サーバーにメッセージを残す場合は <tt>true</tt>
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>message</em> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws MqttRuntimeException		セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishString(MqttSession session, String topic, String message, int qos, boolean retained) {
		publishString(session, topic, message, null, qos, retained);
	}
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたメッセージを <code>Windows<code> 標準シフトJIS（MS932）文字コードのバイトデータとしてパブリッシュする。
	 * <p>このメソッドでは、サーバーにメッセージを残さず、メッセージ伝達品質に 1 を指定してパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param message		送信するメッセージ
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>message</em> のどれかが <tt>null</tt> の場合
	 * @throws MqttRuntimeException		文字コードがサポートされていない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishStringSJIS(MqttSession session, String topic, String message) {
		publishString(session, topic, message, "MS932", 1, false);
	}
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたメッセージを <code>Windows</code> 標準シフトJIS（MS932）文字コードのバイトデータとしてパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param message		送信するメッセージ
	 * @param qos			メッセージ伝達品質（0, 1, 2）
	 * @param retained		サーバーにメッセージを残す場合は <tt>true</tt>
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>message</em> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws MqttRuntimeException		文字コードがサポートされていない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishStringSJIS(MqttSession session, String topic, String message, int qos, boolean retained) {
		publishString(session, topic, message, "MS932", qos, retained);
	}
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたメッセージを <code>UTF-8</code> 文字コードのバイトデータとしてパブリッシュする。
	 * <p>このメソッドでは、サーバーにメッセージを残さず、メッセージ伝達品質に 1 を指定してパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param message		送信するメッセージ
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>message</em> のどれかが <tt>null</tt> の場合
	 * @throws MqttRuntimeException		文字コードがサポートされていない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishStringUTF8(MqttSession session, String topic, String message) {
		publishString(session, topic, message, "UTF-8", 1, false);
	}
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたメッセージを <code>UTF-8</code> 文字コードのバイトデータとしてパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param message		送信するメッセージ
	 * @param qos			メッセージ伝達品質（0, 1, 2）
	 * @param retained		サーバーにメッセージを残す場合は <tt>true</tt>
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>message</em> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws MqttRuntimeException		文字コードがサポートされていない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishStringUTF8(MqttSession session, String topic, String message, int qos, boolean retained) {
		publishString(session, topic, message, "UTF-8", qos, retained);
	}
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたメッセージを <em>charsetName</em> に指定された文字コードのバイトデータとしてパブリッシュする。
	 * <em>charsetName</em> が <tt>null</tt> もしくは空文字の場合、プラットフォーム標準文字コードのバイトデータとしてパブリッシュする。
	 * <p>このメソッドでは、サーバーにメッセージを残さず、メッセージ伝達品質に 1 を指定してパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param message		送信するメッセージ
	 * @param charsetName	文字コード名
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>message</em> のどれかが <tt>null</tt> の場合
	 * @throws MqttRuntimeException		指定された文字コードがサポートされていない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishString(MqttSession session, String topic, String message, String charsetName) {
		publishString(session, topic, message, charsetName, 1, false);
	}

	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたメッセージを <em>charsetName</em> に指定された文字コードのバイトデータとしてパブリッシュする。
	 * <em>charsetName</em> が <tt>null</tt> もしくは空文字の場合、プラットフォーム標準文字コードのバイトデータとしてパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param message		送信するメッセージ
	 * @param charsetName	文字コード名
	 * @param qos			メッセージ伝達品質（0, 1, 2）
	 * @param retained		サーバーにメッセージを残す場合は <tt>true</tt>
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>message</em> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws MqttRuntimeException		指定された文字コードがサポートされていない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishString(MqttSession session, String topic, String message, String charsetName, int qos, boolean retained) {
		// check
		if (session == null)
			throw new NullPointerException("session object is null.");
		if (topic == null)
			throw new NullPointerException("topic string is null.");
		if (message == null)
			throw new NullPointerException("message string is null.");
		
		// create payload
		byte[] payload;
		if (charsetName != null && charsetName.length() > 0) {
			try {
				payload = message.getBytes(charsetName);
			} catch (UnsupportedEncodingException ex) {
				throw new MqttRuntimeException("Unsupported character-set : " + String.valueOf(charsetName), ex);
			}
		} else {
			payload = message.getBytes();
		}
		
		// publish
		session.publishAndWait(topic, payload, qos, retained);
	}
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたファイルの内容をバイナリデータとしてパブリッシュする。
	 * <br>このメソッドでパブリッシュ可能なファイルサイズは、理論上、<code>2,147,483,647</code> バイト（およそ、<code>1.99GB</code>）までとなる。
	 * <p>このメソッドでは、サーバーにメッセージを残さず、メッセージ伝達品質に 1 を指定してパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param filename		送信するファイル
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>filename</em> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	ファイルサイズが大きすぎる場合
	 * @throws MqttRuntimeException		ファイルが読み込めない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishBinaryFile(MqttSession session, String topic, String filename) {
		publishBinaryFile(session, topic, filename, 1, false);
	}

	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたファイルの内容をバイナリデータとしてパブリッシュする。
	 * <br>このメソッドでパブリッシュ可能なファイルサイズは、理論上、<code>2,147,483,647</code> バイト（およそ、<code>1.99GB</code>）までとなる。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param filename		送信するファイル
	 * @param qos			メッセージ伝達品質（0, 1, 2）
	 * @param retained		サーバーにメッセージを残す場合は <tt>true</tt>
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>filename</em> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合、もしくは、ファイルサイズが大きすぎる場合
	 * @throws MqttRuntimeException		ファイルが読み込めない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishBinaryFile(MqttSession session, String topic, String filename, int qos, boolean retained) {
		// check
		if (session == null)
			throw new NullPointerException("session object is null.");
		if (topic == null)
			throw new NullPointerException("topic string is null.");
		if (filename == null)
			throw new NullPointerException("filename is null.");
		
		// create payload
		byte[] payload = getPayloadFromBinaryFile(filename);
		
		// publish
		session.publishAndWait(topic, payload, qos, retained);
	}
	
	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたファイルの内容をテキストデータとしてパブリッシュする。
	 * パブリッシュするデータは、<em>fileCharsetName</em> に指定された文字コードでファイルから読み込まれ、
	 * <em>messageCharsetName</em> に指定された文字コードのバイトデータに変換される。
	 * <em>messageCharsetName</em>、<em>fileCharsetName</em> は、<tt>null</tt> もしくは空文字が指定された場合、
	 * それぞれプラットフォーム標準の文字コードが使用される。
	 * <br>このメソッドでパブリッシュ可能なファイルサイズは、理論上、<code>2,147,483,647</code> バイト（およそ、<code>1.99GB</code>）までとなる。
	 * <p>このメソッドでは、サーバーにメッセージを残さず、メッセージ伝達品質に 1 を指定してパブリッシュする。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param filename		送信するファイル
	 * @param messageCharsetName	ファイルから読み込まれたテキストの変換に適用する文字コード名
	 * @param fileCharsetName	ファイル読み込み時に適用する文字コード名
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>filename</em> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	ファイルサイズが大きすぎる場合
	 * @throws MqttRuntimeException		指定された文字コードがサポートされていない場合、ファイルが読み込めない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishTextFile(MqttSession session, String topic, String filename, String messageCharsetName, String fileCharsetName) {
		publishTextFile(session, topic, filename, messageCharsetName, fileCharsetName, 1, false);
	}

	/**
	 * <em>topic</em> に指定されたトピックに対し、指定されたファイルの内容をテキストデータとしてパブリッシュする。
	 * パブリッシュするデータは、<em>fileCharsetName</em> に指定された文字コードでファイルから読み込まれ、
	 * <em>messageCharsetName</em> に指定された文字コードのバイトデータに変換される。
	 * <em>messageCharsetName</em>、<em>fileCharsetName</em> は、<tt>null</tt> もしくは空文字が指定された場合、
	 * それぞれプラットフォーム標準の文字コードが使用される。
	 * <br>このメソッドでパブリッシュ可能なファイルサイズは、理論上、<code>2,147,483,647</code> バイト（およそ、<code>1.99GB</code>）までとなる。
	 * @param session		パブリッシュに使用するセッション
	 * @param topic			メッセージの宛先とするトピック
	 * @param filename		送信するファイル
	 * @param messageCharsetName	ファイルから読み込まれたテキストの変換に適用する文字コード名
	 * @param fileCharsetName	ファイル読み込み時に適用する文字コード名
	 * @param qos			メッセージ伝達品質（0, 1, 2）
	 * @param retained		サーバーにメッセージを残す場合は <tt>true</tt>
	 * @throws NullPointerException		<em>session</em>、<em>topic</em>、<em>filename</em> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合、もしくは、ファイルサイズが大きすぎる場合
	 * @throws MqttRuntimeException		指定された文字コードがサポートされていない場合、ファイルが読み込めない場合、セッションがサーバーに接続されていない場合、もしくは、パブリッシュに失敗した場合
	 */
	static public void publishTextFile(MqttSession session, String topic, String filename, String messageCharsetName, String fileCharsetName, int qos, boolean retained) {
		// check
		if (session == null)
			throw new NullPointerException("session object is null.");
		if (topic == null)
			throw new NullPointerException("topic string is null.");
		if (filename == null)
			throw new NullPointerException("filename is null.");
		
		// create payload
		byte[] payload = getPayloadFromTextFile(filename, messageCharsetName, fileCharsetName);
		
		// publish
		session.publishAndWait(topic, payload, qos, retained);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected byte[] getPayloadFromBinaryFile(String filename) {
		assert (filename != null);
		byte[] result = null;
		FileInputStream fis = null;
		File targetFile = new File(filename);
		
		// check file size
		if (targetFile.length() > (long)Integer.MAX_VALUE) {
			String errmsg = "Failed to publish, Target file size is too large : \"" + String.valueOf(filename) + "\"";
			throw new IllegalArgumentException(errmsg);
		}

		// read file
		try {
			int remainSize = (int)targetFile.length();
			result = new byte[remainSize];
			fis = new FileInputStream(targetFile);
			fis.read(result);
		}
		catch (FileNotFoundException ex) {
			throw new MqttRuntimeException("Failed to publish, File not found : \"" + String.valueOf(filename) + "\"", ex);
		}
		catch (Throwable ex) {
			throw new MqttRuntimeException("Failed to read from file : \"" + String.valueOf(filename) + "\"", ex);
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Throwable ignoreEx) {ignoreEx=null;}
				fis = null;
			}
		}
		return result;
	}
	
	static protected byte[] getPayloadFromTextFile(String filename, String messageCharsetName, String fileCharsetName) {
		assert (filename != null);
		Charset csMessage, csFile;
		
		// setup encoding
		//--- for message
		if (messageCharsetName != null && messageCharsetName.length() > 0) {
			try {
				csMessage = Charset.forName(messageCharsetName);
			} catch (Throwable ex) {
				throw new MqttRuntimeException("Unsupported character-set for message : " + String.valueOf(messageCharsetName), ex);
			}
		} else {
			csMessage = Charset.defaultCharset();
		}
		//--- for file
		if (fileCharsetName != null && fileCharsetName.length() > 0) {
			try {
				csFile = Charset.forName(fileCharsetName);
			} catch (Throwable ex) {
				throw new MqttRuntimeException("Unsupported character-set for reading file : " + String.valueOf(fileCharsetName), ex);
			}
		} else {
			csFile = Charset.defaultCharset();
		}
		
		// read file
		byte[] result = getPayloadFromBinaryFile(filename);
		
		// convert encoding
		if (result != null && result.length > 0) {
			String str = new String(result, csFile);
			result = null;
			result = str.getBytes(csMessage);
		}
		
		return result;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
