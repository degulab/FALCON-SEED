/*
 * @(#)MqttManagedSession.java	0.2.0	2013/05/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.internal;

import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.MqttRuntimeException;

/**
 * JavaVM 終了時に接続を切断するための、制御機能を組み込んだ、MQTT セッションの実装。
 * 
 * @version 0.2.0	2013/05/02
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttManagedSessionImpl extends MqttSessionImpl
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

	/**
	 * 指定された接続パラメータで、新しいインスタンスを生成する。
	 * @param params	接続パラメータ
	 * @throws NullPointerException	<em>params</em> が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションオブジェクトが生成できない場合
	 */
	public MqttManagedSessionImpl(MqttConnectionParams params) {
		super(params);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void onConnected() {
		MqttManager.getInstance().add(this);
		super.onConnected();
	}

	@Override
	protected void onDisconnected() {
		MqttManager.getInstance().remove(this);
		super.onDisconnected();
	}

	@Override
	protected void onConnectionLost(Throwable cause) {
		if (!isConnected()) {
			MqttManager.getInstance().remove(this);
		}
		super.onConnectionLost(cause);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
