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
