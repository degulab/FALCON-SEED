package ssac.falconseed.mqtt.broker;

import static org.junit.Assert.*;

import org.junit.Test;

import ssac.aadl.runtime.mqtt.MqttBufferedSession;
import ssac.aadl.runtime.mqtt.MqttUtil;

public class MoquetteBrokerLauncherTest
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

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------
	
	@Test
	public void testBrokerInprocess() throws Exception
	{
		// start broker
		assertNull(MoquetteBrokerLauncher._inprocServer);
		MoquetteBrokerLauncher.startBrokerInprocess();
		assertNotNull(MoquetteBrokerLauncher._inprocServer);
		
		// start broker, already started
		MoquetteBrokerLauncher.startBrokerInprocess();
		assertNotNull(MoquetteBrokerLauncher._inprocServer);
		
		// check connection
		MqttBufferedSession session = MqttUtil.connectBufferedSession();
		assertTrue(session.isConnected());
		//--- disconnect
		session.disconnect();
		
		// stop broker
		assertNotNull(MoquetteBrokerLauncher._inprocServer);
		MoquetteBrokerLauncher.stopBrokerInprocess();
		assertNull(MoquetteBrokerLauncher._inprocServer);
		
		// stop broker, already stopped
		MoquetteBrokerLauncher.stopBrokerInprocess();
		assertNull(MoquetteBrokerLauncher._inprocServer);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
