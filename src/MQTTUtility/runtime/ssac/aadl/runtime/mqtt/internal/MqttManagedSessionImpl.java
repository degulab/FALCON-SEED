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
