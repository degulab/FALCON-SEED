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
 * @(#)MqttArrivedMessage.java	0.3.0	2013/06/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttArrivedMessage.java	0.2.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


/**
 * MQTT セッションが受信したメッセージデータ。
 * このオブジェクトは不変オブジェクトである。
 * 
 * @version 0.3.0	2013/06/27
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttArrivedMessage
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final MqttArrivedMessage INVALID_MESSAGE = new MqttArrivedMessage(null, (MqPayload)null, 0, false, false);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String		_topic;
	private final MqPayload		_payload;
	private final int			_qos;
	private final boolean		_retained;
	private final boolean		_duplicated;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param topic			トピック
	 * @param payload		受信メッセージデータ
	 * @param qos			メッセージ伝達品質(QoS)
	 * @param retained		サーバーに保留されていたメッセージなら <tt>true</tt>
	 * @param duplicated	重複の可能性があるメッセージの場合は <tt>true</tt>
	 */
	public MqttArrivedMessage(String topic, MqPayload payload, int qos, boolean retained, boolean duplicated) {
		this._topic      = (topic==null ? "" : topic);
		this._payload    = (payload==null ? MqPayload.EmptyPayload : payload);
		this._qos        = qos;
		this._retained   = retained;
		this._duplicated = duplicated;
	}
	
	/**
	 * 指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param topic			トピック
	 * @param array			受信データのバイト配列
	 * @param qos			メッセージ伝達品質(QoS)
	 * @param retained		サーバーに保留されていたメッセージなら <tt>true</tt>
	 * @param duplicated	重複の可能性があるメッセージの場合は <tt>true</tt>
	 */
	public MqttArrivedMessage(String topic, byte[] array, int qos, boolean retained, boolean duplicated) {
		this(topic, new MqPayload(array), qos, retained, duplicated);
	}

	/**
	 * 指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param topic			トピック
	 * @param array			受信データのバイト配列
	 * @param offset		バイト配列の有効データ開始位置を示すオフセット
	 * @param length		バイト配列の有効データ長とするバイト数
	 * @param qos			メッセージ伝達品質(QoS)
	 * @param retained		サーバーに保留されていたメッセージなら <tt>true</tt>
	 * @param duplicated	重複の可能性があるメッセージの場合は <tt>true</tt>
	 * @throws IndexOutOfBoundsException	<em>offset</em> および <em>length</em> の値が適切ではない場合
	 */
	public MqttArrivedMessage(String topic, byte[] array, int offset, int length, int qos, boolean retained, boolean duplicated) {
		this(topic, new MqPayload(array, offset, length), qos, retained, duplicated);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * メッセージ伝達品質の値を検証する。
	 * @param qos	メッセージ伝達品質（0, 1, 2）
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 */
	static public void validQos(int qos) {
		if ((qos < 0) || (qos > 2)) {
			throw new IllegalArgumentException("Invalid QoS value : " + qos);
		}
	}

	/**
	 * トピックもメッセージデータも存在しない場合に <tt>true</tt> を返す。
	 * @return	トピックもメッセージデータも存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isInvalid() {
		return (_topic.length() <= 0 && _payload.isEmpty());
	}

	/**
	 * メッセージデータが存在しない場合に <tt>true</tt> を返す。
	 * @return	メッセージデータが存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEmpty() {
		return (_payload.isEmpty());
	}

	/**
	 * このメッセージの属するトピックを返す。
	 * @return	トピックを示す文字列
	 */
	public String getTopic() {
		return _topic;
	}

	/**
	 * メッセージデータを返す。
	 * @return	<code>MqPayload</code> オブジェクト
	 */
	public MqPayload getPayload() {
		return _payload;
	}

	/**
	 * このメッセージの伝達品質(QoS)を返す。
	 * @return	伝達品質(QoS)
	 */
	public int getQos() {
		return _qos;
	}

	/**
	 * このメッセージがサーバーに保留されていたメッセージであれば <tt>true</tt> を返す。
	 * @return	サーバーに保留されていたメッセージなら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isRetained() {
		return _retained;
	}

	/**
	 * このメッセージがすでに受信したメッセージと重複している可能性がある場合に <tt>true</tt> を返す。
	 * @return	重複の可能性があるメッセージの場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isDuplicated() {
		return _duplicated;
	}

	/**
	 * メッセージを、プラットフォーム標準の文字コードで文字列に変換する。
	 * @return	文字列に変換されたメッセージ、メッセージデータが存在しない場合は空文字を返す。
	 */
	public String getMessageString() {
		return getMessageString(null);
	}

	/**
	 * メッセージを、<code>Windows</code> 標準のシフトJIS（MS932）文字コードで文字列に変換する。
	 * @return	文字列に変換されたメッセージ、メッセージデータが存在しない場合は空文字を返す。
	 * @throws MqttRuntimeException	文字コードがサポートされていない場合
	 */
	public String getMessageStringSJIS() {
		return getMessageString("MS932");
	}

	/**
	 * メッセージを、<code>UTF-8</code> 文字コードで文字列に変換する。
	 * @return	文字列に変換されたメッセージ、メッセージデータが存在しない場合は空文字を返す。
	 * @throws MqttRuntimeException	文字コードがサポートされていない場合
	 */
	public String getMessageStringUTF8() {
		return getMessageString("UTF-8");
	}

	/**
	 * メッセージを、指定された文字コードで文字列に変換する。
	 * <em>charsetName</em> が <tt>null</tt> もしくは空文字の場合、プラットフォーム標準の文字コードで変換する。
	 * @param charsetName	文字コード名
	 * @return	文字列に変換されたメッセージ、メッセージデータが存在しない場合は空文字を返す。
	 * @throws MqttRuntimeException	指定された文字コードがサポートされていない場合
	 */
	public String getMessageString(String charsetName) {
		if (isEmpty()) {
			return "";
		}
		
		if (charsetName != null && charsetName.length() > 0) {
			try {
				return _payload.toString(charsetName);
			} catch (UnsupportedEncodingException ex) {
				throw new MqttRuntimeException("Unsupported character-set : " + String.valueOf(charsetName), ex);
			}
		} else {
			return _payload.toString();
		}
	}

	/**
	 * メッセージを、指定されたファイルにバイナリデータのまま出力する。
	 * @param filename	出力先のファイル名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	出力に失敗した場合
	 */
	public void saveMessageToBinaryFile(String filename) {
		if (filename == null)
			throw new NullPointerException("filename is null.");
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filename));
			if (!_payload.isEmpty()) {
				fos.write(_payload.getData(), _payload.getOffset(), _payload.getLength());
			}
		}
		catch (FileNotFoundException ex) {
			throw new MqttRuntimeException("File not found : \"" + String.valueOf(filename) + "\"", ex);
		}
		catch (Throwable ex) {
			throw new MqttRuntimeException("Failed to write message to file : \"" + String.valueOf(filename) + "\"", ex);
		}
		finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Throwable ignoreEx) {ignoreEx=null;}
				fos = null;
			}
		}
	}
	
	/**
	 * メッセージを <em>messageCharsetName</em> に指定された文字コードで文字列に変換し、
	 * <em>fileCharsetName</em> に指定された文字コードでファイルに出力する。
	 * <em>messageCharsetName</em> および <em>fileCharsetName</em> は、
	 * 指定された文字列が <tt>null</tt> もしくは空文字の場合、プラットフォーム標準の文字コードが使用される。
	 * @param filename	出力先のファイル名
	 * @param messageCharsetName	メッセージを文字列に変換する際の文字コード名
	 * @param fileCharsetName		ファイル出力時の文字コード名
	 * @throws NullPointerException	<em>filename</em> が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	指定された文字コードがサポートされていない場合、または、出力に失敗した場合
	 */
	public void saveMessageToTextFile(String filename, String messageCharsetName, String fileCharsetName) {
		if (filename == null)
			throw new NullPointerException("filename is null.");
		
		char[] buffer = new char[8192];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(_payload.getData(), _payload.getOffset(), _payload.getLength());
		InputStreamReader isr;
		if (messageCharsetName != null && messageCharsetName.length() > 0) {
			try {
				isr = new InputStreamReader(bais, messageCharsetName);
			} catch (UnsupportedEncodingException ex) {
				throw new MqttRuntimeException("Unsupported character-set for the message : " + messageCharsetName, ex);
			}
		} else {
			isr = new InputStreamReader(bais);
		}
		
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(new File(filename));
			if (fileCharsetName != null && fileCharsetName.length() > 0) {
				osw = new OutputStreamWriter(fos, fileCharsetName);
			} else {
				osw = new OutputStreamWriter(fos);
			}
			
			for (;;) {
				int read = isr.read(buffer);
				if (read < 0)
					break;
				osw.write(buffer, 0, read);
			}
			osw.flush();
		}
		catch (FileNotFoundException ex) {
			throw new MqttRuntimeException("File not found : \"" + String.valueOf(filename) + "\"", ex);
		}
		catch (UnsupportedEncodingException ex) {
			throw new MqttRuntimeException("Unsupported character-set for the file : " + fileCharsetName, ex);
		}
		catch (Throwable ex) {
			throw new MqttRuntimeException("Failed to write message to file : \"" + String.valueOf(filename) + "\"", ex);
		}
		finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (Throwable ignoreEx) {ignoreEx=null;}
				osw = null;
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Throwable ignoreEx) {ignoreEx=null;}
				fos = null;
			}
			try {
				isr.close();
			} catch (Throwable ignoreEx) {ignoreEx=null;}
			isr = null;
			bais = null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
