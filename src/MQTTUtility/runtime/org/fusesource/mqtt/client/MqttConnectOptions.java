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
 * @(#)MqttConnectOptions.java	0.3.0	2013/06/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package org.fusesource.mqtt.client;


/**
 * MQTT connect options.
 * 
 * @version 0.3.0	2013/06/27
 * @since 0.3.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttConnectOptions extends MQTT
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private int connectionTimeout = 30;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MqttConnectOptions() {
		super();
		setCleanSession(true);
		setKeepAlive((short)60);
		setConnectAttemptsMax(0);	// default : no retry connection
		setReconnectAttemptsMax(0);	// default : no re-connection
	}

	protected MqttConnectOptions(MQTT other) {
		super(other);
		if (other instanceof MqttConnectOptions) {
			this.connectionTimeout = ((MqttConnectOptions)other).connectionTimeout;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	public int getKeepAliveInterval() {
		return getKeepAlive();
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		setKeepAlive((short)keepAliveInterval);
	}

	@Override
	public MqttCallbackConnection callbackConnection() {
		if (!isCleanSession() && getClientId()==null) {
			throw new IllegalArgumentException("The client ID must be configured when clean session is set to false.");
		}
		return new MqttCallbackConnection(new MqttConnectOptions(this));
	}
	
	public MqttCallbackConnection callbackConnection(Tracer newTracer) {
		if (!isCleanSession() && getClientId()==null) {
			throw new IllegalArgumentException("The client ID must be configured when clean session is set to false.");
		}
		
		// create dupulicated options
		MqttConnectOptions newOptions = new MqttConnectOptions(this);
		if (newTracer != null) {
			newOptions.setTracer(newTracer);
		}
		return new MqttCallbackConnection(newOptions);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
