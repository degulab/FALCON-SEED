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
