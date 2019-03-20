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
 * @(#)MqttCallbackConnection.java	0.3.0	2013/06/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package org.fusesource.mqtt.client;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import org.fusesource.hawtdispatch.transport.DefaultTransportListener;
import org.fusesource.hawtdispatch.transport.Transport;
import org.fusesource.hawtdispatch.transport.TransportListener;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.fusesource.mqtt.codec.PUBLISH;

/**
 * MQTT-client custom callback object.
 * 
 * @version 0.3.0	2013/06/30
 * @since 0.3.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttCallbackConnection extends CallbackConnection
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
	
	public MqttCallbackConnection(MQTT mqtt) {
		super(mqtt);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public int qosTypeToValue(QoS qos) {
		if (qos == QoS.EXACTLY_ONCE) {
			return 2;
		}
		else if (qos == QoS.AT_LEAST_ONCE) {
			return 1;
		}
		else if (qos == QoS.AT_MOST_ONCE) {
			return 0;
		}
		else {
			return (-1);
		}
	}
	
	static public QoS qosValueToType(int qos) {
		if (qos == 2) {
			return QoS.EXACTLY_ONCE;
		}
		else if (qos == 1) {
			return QoS.AT_LEAST_ONCE;
		}
		else if (qos == 0) {
			return QoS.AT_MOST_ONCE;
		}
		else {
			return null;
		}
	}

	@Override
	public void onSessionEstablished(Transport transport) {
		// 念のため、受信をサスペンド
		int spCount = getSuspendCount();
		if (spCount <= 0) {
			transport.suspendRead();
		}

		// 親クラスの機能
		super.onSessionEstablished(transport);
		
		// トランスポートリスナー変更
		DelegateTransportListener listener = new DelegateTransportListener(transport.getTransportListener());
		//DelegateTransportListener listener = new DelegateTransportListener(getTransportListener(transport));
		transport.setTransportListener(listener);
		
		// 受信を再開
		if (spCount <= 0) {
			transport.resumeRead();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void forceKillConnection() {
		setDisconnectedFlag(true);
		kill(null);
	}
	
	protected boolean getDisconnectedFlag() {
		try {
			Field f = CallbackConnection.class.getDeclaredField("disconnected");
			f.setAccessible(true);
			return ((Boolean)f.get(this)).booleanValue();
		} catch (Throwable ignoreEx) {
			return false;
		}
	}
	
	protected void setDisconnectedFlag(boolean toDisconnected) {
		try {
			Field f = CallbackConnection.class.getDeclaredField("disconnected");
			f.setAccessible(true);
			f.set(this, toDisconnected);
		} catch (Throwable ignoreEx) {}
	}
	
	protected int getSuspendCount() {
		try {
			Field f = CallbackConnection.class.getDeclaredField("suspendCount");
			f.setAccessible(true);
			return ((AtomicInteger)f.get(this)).get();
		} catch (Throwable ignoreEx) {
			return 0;
		}
	}
	
	protected Listener getListener() {
		try {
			Field f = CallbackConnection.class.getDeclaredField("listener");
			f.setAccessible(true);
			return ((Listener)f.get(this));
		} catch (Throwable ignoreEx) {
			return null;
		}
	}
	
//	protected TransportListener getTransportListener(final Transport transport) {
//		if (transport == null)
//			return null;
//		
//		// find getTransportListener interface
//		try {
//			Method m = transport.getClass().getDeclaredMethod("getTransportListener");
//			return (TransportListener)m.invoke(transport);
//		} catch (Throwable ignoreEx) {}
//		
//		// find local listener field
//		try {
//			Field f = transport.getClass().getDeclaredField("listener");
//			f.setAccessible(true);
//			return ((TransportListener)f.get(transport));
//		} catch (Throwable ignoreEx) {}
//		
//		throw new UnsupportedOperationException("Could not get the current TransportListener object from Transport instance!");
//	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public interface MqttMessageListener extends Listener
	{
		public int getQos();
		public QoS getQosType();
		public void setQosType(QoS qos);
		
		public boolean isRetained();
		public void setRetained(boolean retained);
		
		public boolean isDuplicated();
		public void setDuplicated(boolean duplicated);
	}
	
	protected class DelegateTransportListener extends DefaultTransportListener
	{
		protected final TransportListener	_orgListener;
		
		public DelegateTransportListener(TransportListener listener) {
			_orgListener = listener;
		}

		@Override
		public void onTransportCommand(Object command) {
			Listener l = getListener();
			if (l instanceof MqttMessageListener) {
				MQTTFrame frame = (MQTTFrame) command;
				if (frame.messageType() == PUBLISH.TYPE) {
					MqttMessageListener msglistener = (MqttMessageListener)l;
					msglistener.setQosType(frame.qos());
					msglistener.setRetained(frame.retain());
					msglistener.setDuplicated(frame.dup());
				}
			}
			_orgListener.onTransportCommand(command);
		}

		@Override
		public void onRefill() {
			_orgListener.onRefill();
		}

		@Override
		public void onTransportFailure(IOException error) {
			_orgListener.onTransportFailure(error);
		}
	}
}
