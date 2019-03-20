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
 * @(#)AADLMqttFunctions.java	0.1.0	2013/03/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.internal;

import java.math.BigDecimal;
import java.nio.charset.Charset;

/**
 * AADL で publish もしくは subscribe を行うためのインタフェース群。
 * 
 * @version 0.1.0	2013/03/12
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class AADLMqttFunctions
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** CSVファイルのデフォルトエンコーディング用プロパティキー **/
	static public final String PROPKEY_AADL_CSV_ENCODING = "aadl.csv.encoding";

	/** メッセージ・エンコーディング：バイナリ **/
	static protected final String MSGTYPE_BINARY	= "binary";
	/** メッセージ・エンコーディング：プラットフォーム標準文字コード **/
	static protected final String MSGTYPE_PLATFORM	= "platform";
	/** メッセージ・エンコーディング：標準文字コード（CSV encoding） **/
	static protected final String MSGTYPE_DEFAULT	= "default";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLMqttFunctions() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された文字列が <tt>null</tt> ではなく、空文字列でもないことを検証する。
	 * @param value		検証する文字列
	 * @param errmsg	例外のメッセージ
	 * @throws IllegalArgumentException	<em>value</em> が <tt>null</tt> もしくは空文字列の場合
	 */
	static public void validNotEmptyString(String value, String errmsg) {
		if (value == null || value.length() <= 0) {
			if (errmsg != null && errmsg.length() > 0) {
				throw new IllegalArgumentException(errmsg);
			} else {
				throw new IllegalArgumentException("String is empty.");
			}
		}
	}

	/**
	 * 指定された文字列の前後空白を除外したときに空文字列ではないことを検証する。
	 * @param value		検証する文字列
	 * @param errmsg	例外のメッセージ
	 * @throws IllegalArgumentException	<em>value</em> が <tt>null</tt>、もしくは前後空白を除外したときに空文字列の場合
	 */
	static public void validNotEmptyTrimmedString(String value, String errmsg) {
		if (value != null)
			value = value.trim();
		if (value == null || value.length() <= 0) {
			if (errmsg != null && errmsg.length() > 0) {
				throw new IllegalArgumentException(errmsg);
			} else {
				throw new IllegalArgumentException("Trimmed string is empty.");
			}
		}
	}

	/**
	 * 指定された文字セット名が <code>CSV</code> ファイルエンコーディングとして適切な文字セット名かを検証し、
	 * 有効な文字セット名を返す。
	 * 文字セット名の検証では、&quot;binary&quot;、&quot;platform&quot;、&quot;default&quot; も適切と判定し、
	 * 対応する文字セット名に変換する。
	 * @param encoding	検証する文字セット名
	 * @param errmsg	例外のメッセージ
	 * @return	有効な文字セット名
	 * @throws IllegalArgumentException	<em>encoding</em> が文字セット名として適切ではない場合
	 */
	static public String validCsvEncoding(String encoding, String errmsg) {
		String name = getDefaultCsvEncoding();
		if (encoding != null) {
			encoding = encoding.trim();
			if (encoding.length() > 0) {
				if (MSGTYPE_BINARY.equalsIgnoreCase(encoding)) {
					// binary message type
					return null;
				}
				else if (MSGTYPE_PLATFORM.equalsIgnoreCase(encoding)) {
					// The encoding is a platform-independent.
					name = null;
				}
				else if (!MSGTYPE_DEFAULT.equalsIgnoreCase(encoding)) {
					// Specified encoding charset name, check available
					try {
						Charset.forName(name);
					}
					catch (Throwable ex) {
						throw new IllegalArgumentException("Unsupported encoding : " + name, ex);
					}
					name = encoding;
				}
			}
		}
		
		if (name == null || name.length() <= 0) {
			// use platform-independent encoding
			name = Charset.defaultCharset().name();
		}
		return name;
	}
	
	/**
	 * 指定された文字セット名がメッセージエンコーディングとして適切な文字セット名かを検証し、
	 * 有効な文字セット名を返す。
	 * 文字セット名の検証では、&quot;binary&quot;、&quot;platform&quot;、&quot;default&quot; も適切と判定し、
	 * 対応する文字セット名に変換する。
	 * @param msgEncoding	検証する文字セット名
	 * @param errmsg	例外のメッセージ
	 * @return	有効な文字セット名
	 * @throws IllegalArgumentException	<em>msgEncoding</em> が文字セット名として適切ではない場合
	 */
	static public String validMessageEncoding(String msgEncoding, String errmsg) {
		String name = null;
		if (msgEncoding  != null) {
			msgEncoding = msgEncoding.trim();
			if (msgEncoding.length() > 0) {
				if (MSGTYPE_BINARY.equalsIgnoreCase(msgEncoding)) {
					// binary message type
					return null;
				}
				else if (MSGTYPE_PLATFORM.equalsIgnoreCase(msgEncoding)) {
					// The encoding is a platform-independent.
					name = Charset.defaultCharset().name();
				}
				else if (!MSGTYPE_DEFAULT.equalsIgnoreCase(msgEncoding)) {
					// Specified encoding charset name, check available
					try {
						Charset.forName(msgEncoding);
					}
					catch (Throwable ex) {
						if (errmsg != null && errmsg.length() > 0) {
							throw new IllegalArgumentException(errmsg + " : " + msgEncoding, ex);
						} else {
							throw new IllegalArgumentException("Unsupported message encoding : " + msgEncoding, ex);
						}
					}
					name = msgEncoding;
				}
			}
		}
		return name;
	}

	/**
	 * 指定された文字列が、メッセージ伝達品質（<code>QoS</code>）として適切な値かを検証し、
	 * 数値に変換された値を返す。
	 * @param value		検証する値
	 * @param errmsg	例外のメッセージ
	 * @return	数値に変換されたメッセージ伝達品質を示す値
	 * @throws IllegalArgumentException	<em>value</em> がメッセージ伝達品質の値として適切ではない場合
	 */
	static public int validQosParam(String value, String errmsg) {
		int qos = 1;
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				try {
					qos = Integer.valueOf(value);
				}
				catch (Throwable ex) {
					if (errmsg != null && errmsg.length() > 0) {
						throw new IllegalArgumentException(errmsg + " : " + value, ex);
					} else {
						throw new IllegalArgumentException("Invalid QoS value ( 0, 1 or 2 ) : " + value, ex);
					}
				}
				if (qos < 0 || qos > 2) {
					if (errmsg != null && errmsg.length() > 0) {
						throw new IllegalArgumentException(errmsg + " : " + value);
					} else {
						throw new IllegalArgumentException("Invalid QoS value ( 0, 1 or 2 ) : " + value);
					}
				}
			}
		}
		return qos;
	}

	/**
	 * 指定された文字列を真偽値に変換する。
	 * このメソッドでは、文字列の真偽値への変換に <code>java.lang.Boolean.valueOf</code> メソッドを使用する。
	 * @param value	変換する文字列
	 * @return	真偽値
	 */
	static public boolean toBoolean(String value) {
		if (value != null)
			value = value.trim();
		return Boolean.valueOf(value);
	}

	/**
	 * 指定された文字列を真偽値に変換する。
	 * このメソッドでは、指定された文字列が &quot;true&quot; もしくは &quot;false&quot; 以外の場合は
	 * 例外をスローする。なお、大文字小文字は区別しない。
	 * @param value		変換する文字列
	 * @param errmsg	例外のメッセージ
	 * @return	真偽値
	 * @throws IllegalArgumentException	<em>value</em> が真偽値ではない場合
	 */
	static public boolean toBooleanExact(String value, String errmsg) {
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				if (value.equalsIgnoreCase("true")) {
					return true;
				}
				else if (value.equalsIgnoreCase("false")) {
					return false;
				}
				if (errmsg != null && errmsg.length() > 0) {
					throw new IllegalArgumentException(errmsg + " : " + value);
				} else {
					throw new IllegalArgumentException("Invalid boolean value ( 'true' or 'false' ) : " + value);
				}
			}
		}
		if (errmsg != null && errmsg.length() > 0) {
			throw new IllegalArgumentException(errmsg);
		} else {
			throw new IllegalArgumentException("Invalid boolean value ( 'true' or 'false' ).");
		}
	}

	/**
	 * 指定された文字列を実数値に変換する。
	 * @param value		変換する文字列
	 * @param errmsg	例外のメッセージ
	 * @return	実数値
	 * @throws IllegalArgumentException	<em>value</em> が数値ではない場合
	 */
	static public BigDecimal convertToDecimal(String value, String errmsg) {
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				try {
					BigDecimal retval = new BigDecimal(value);
					return retval;
				}
				catch (Throwable ex) {
					if (errmsg != null && errmsg.length() > 0) {
						throw new IllegalArgumentException(errmsg + " : " + value);
					} else {
						throw new IllegalArgumentException("Invalid decimal value : " + value);
					}
				}
			}
		}
		// no length
		if (errmsg != null && errmsg.length() > 0) {
			throw new IllegalArgumentException(errmsg);
		} else {
			throw new IllegalArgumentException("Invalid decimal value.");
		}
	}

	/**
	 * 指定された文字列を <code>long</code> 型の数値に変換する。
	 * @param value		変換する文字列
	 * @param errmsg	例外のメッセージ
	 * @return	<code>long</code> 型の数値
	 * @throws IllegalArgumentException	<em>value</em> が <code>long</code> 型の数値に変換できない場合
	 */
	static public long convertToLong(String value, String errmsg) {
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				try {
					long retval = Long.valueOf(value);
					return retval;
				}
				catch (Throwable ex) {
					if (errmsg != null && errmsg.length() > 0) {
						throw new IllegalArgumentException(errmsg + " : " + value);
					} else {
						throw new IllegalArgumentException("Invalid long value : " + value);
					}
				}
			}
		}
		// no length
		if (errmsg != null && errmsg.length() > 0) {
			throw new IllegalArgumentException(errmsg);
		} else {
			throw new IllegalArgumentException("Invalid long value.");
		}
	}

	/**
	 * 指定された文字列を <code>long</code> 型の正の整数（0 も含む）に変換する。
	 * このメソッドでは、<code>long</code> 型の正の最大値を超える数値の場合は <code>long</code> 型の正の最大値にクリップされる。
	 * @param value		変換する文字列
	 * @param errmsg	例外のメッセージ
	 * @return	<code>long</code> 型の正の整数
	 * @throws IllegalArgumentException	<em>value</em> が <code>long</code> 型の数値に変換できない場合、もしくは負の値の場合
	 */
	static public long convertToPlusTimeMillis(String value, String errmsg) {
		long retval = 0L;
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				BigDecimal decimal;
				try {
					decimal = new BigDecimal(value);
				}
				catch (Throwable ex) {
					if (errmsg != null && errmsg.length() > 0) {
						throw new IllegalArgumentException(errmsg + " : " + value);
					} else {
						throw new IllegalArgumentException("Invalid decimal value : " + value);
					}
				}
				if (decimal.compareTo(BigDecimal.ZERO) < 0) {
					retval = 0L;
				}
				else if (decimal.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0) {
					retval = Long.MAX_VALUE;
				}
				else {
					retval = decimal.longValue();
				}
			}
		}
		return retval;
	}

	//------------------------------------------------------------
	// Public helpers
	//------------------------------------------------------------

	/**
	 * AADL/ADDL の CSV ファイル入出力において、エンコーディングに使用する
	 * 文字セット名が指定されない場合の、デフォルトのエンコーディングとなる
	 * 文字セット名を返します。
	 * この関数は、システムプロパティ &quot;aadl.csv.encoding&quot; の内容を取得します。
	 * @return	デフォルトの文字セット名。設定されていない場合は <tt>null</tt>。
	 */
	static public final String getDefaultCsvEncoding() {
		String name = System.getProperty(PROPKEY_AADL_CSV_ENCODING);
		return (name!=null && name.length()>0 ? name : null);
	}

	/**
	 * 指定された文字列がメッセージエンコーディングとして適切な文字セット名かを検証し、有効な文字セットを返す。
	 * 文字セット名の検証では、&quot;binary&quot;、&quot;platform&quot;、&quot;default&quot; も適切と判定し、
	 * 対応する文字セットに変換する。なお、&quot;binary&quot; が指定された場合は <tt>null</tt> を返す。
	 * @param msgtype	検証する文字セット名
	 * @return	有効な文字セット
	 * @throws IllegalArgumentException	<em>msgtype</em> が文字セット名として適切ではない場合
	 */
	static public Charset validMessageType(String msgtype) {
		String name = getDefaultCsvEncoding();
		if (msgtype != null) {
			msgtype = msgtype.trim();
			
			if (MSGTYPE_BINARY.equalsIgnoreCase(msgtype)) {
				// binary message type
				return null;
			}
			else if (MSGTYPE_PLATFORM.equalsIgnoreCase(msgtype)) {
				// The message type is a platform-independent.
				return Charset.defaultCharset();
			}
			else if (!MSGTYPE_DEFAULT.equalsIgnoreCase(msgtype)) {
				// Specified encoding charset name
				name = msgtype;
			}
		}
		
		if (name != null) {
			// check available charset
			try {
				return Charset.forName(name);
			}
			catch (Throwable ex) {
				throw new IllegalArgumentException("Unsupported encoding : " + name, ex);
			}
		}
		else {
			// use platform-independent.
			return Charset.defaultCharset();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
