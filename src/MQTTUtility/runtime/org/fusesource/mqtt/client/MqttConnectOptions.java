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
