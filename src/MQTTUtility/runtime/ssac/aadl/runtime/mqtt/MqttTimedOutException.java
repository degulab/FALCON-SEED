/*
 * @(#)MqttTimedOutException.java	0.3.1	2013/07/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttTimedOutException.java	0.3.0	2013/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

/**
 * MQTT における、応答タイムアウト例外。
 * 
 * @version 0.3.1	2013/07/05
 * @since 0.3.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttTimedOutException extends MqttException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String DefaultMessage = "Timed out waiting for a response from the server.";

	private static final long serialVersionUID = 1526779529098966273L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 詳細メッセージに <tt>null</tt> を使用して、新規例外を構築する。
	 */
	public MqttTimedOutException() {
		super(DefaultMessage);
	}

	/**
	 * 指定された詳細メッセージを使用して、新規例外を構築する。
	 * @param message	詳細メッセージ
	 */
	public MqttTimedOutException(String message) {
		super(message);
	}

	/**
	 * 指定された原因と詳細メッセージ <code>(cause==null ? null : cause.toString())</code> を持つ、新規例外を構築する。
	 * @param cause	原因
	 */
	public MqttTimedOutException(Throwable cause) {
		super(DefaultMessage, cause);
	}

	/**
	 * 指定された詳細メッセージおよび原因を使用して、新規例外を構築する。
	 * <em>cause</em> と関連付けられた詳細メッセージが、この実行時例外の詳細メッセージに自動的に統合されることはない。
	 * @param message	詳細メッセージ
	 * @param cause		原因
	 */
	public MqttTimedOutException(String message, Throwable cause) {
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
