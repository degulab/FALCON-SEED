/*
 * @(#)MqttEventHandler.java	0.3.0	2013/06/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttEventHandler.java	0.2.0	2013/05/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttEventHandler.java	0.1.0	2013/03/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.event;

import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttSession;

/**
 * MQTT クライアントで発生したイベントのハンドラ。
 * このハンドラは、MQTT クライアントが管理するスレッドから呼び出されるものもある。
 * 
 * @version 0.3.0	2013/06/27
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public interface MqttEventHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * サーバーとの接続が正常に切断されたときに、このメソッドが呼び出される。
	 * @param session	呼び出し元のセッションオブジェクト
	 */
	public void disconnected(MqttSession session);

	/**
	 * サーバーへの接続が失われた場合、このメソッドが呼び出される。
	 * @param session	呼び出し元のセッションオブジェクト
	 * @param cause	接続が失われた要因
	 */
	public void connectionLost(MqttSession session, Throwable cause);

	/**
	 * メッセージがサーバーから到着したときに、このメソッドが呼び出される。
	 * このメソッドは、MQTT クライアントによって同期的に呼び出される。
	 * このメソッドが正常に完了するまで、承認はサーバーに送信されない。
	 * このメソッドの実装が例外をスローした場合、このクライアントはシャットダウンされる。
	 * <p>
	 * このメソッドでクライアントとの接続を切断してはならない。その場合、デッドロックが発生する。
	 * @param session	呼び出し元のセッションオブジェクト
	 * @param topic		受信したメッセージのトピック
	 * @param message	受信したメッセージ
	 * @throws Exception	何らかのエラーが発生した場合
	 */
	public void messageArrived(MqttSession session, MqttArrivedMessage message) throws Exception;
}
