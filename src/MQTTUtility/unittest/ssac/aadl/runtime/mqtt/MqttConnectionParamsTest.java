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
package ssac.aadl.runtime.mqtt;

import static org.junit.Assert.*;

import org.fusesource.mqtt.client.MqttConnectOptions;
import org.junit.Test;

public class MqttConnectionParamsTest
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
	public void testValidPortNo() {
		MqttConnectionParams.validPortNo(0);
		MqttConnectionParams.validPortNo(1);
		MqttConnectionParams.validPortNo(32767);
		MqttConnectionParams.validPortNo(65535);
		
		try {
			MqttConnectionParams.validPortNo(-1);
			fail("Must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		try {
			MqttConnectionParams.validPortNo(65536);
			fail("Must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
	}

	@Test
	public void testValidClientID() {
		MqttConnectionParams.validClientID("clientID");
		MqttConnectionParams.validClientID("A");
		MqttConnectionParams.validClientID("ABCDEFGHIJKLMNOPQRSTUVW");
		
		try {
			MqttConnectionParams.validClientID(null);
			fail("Must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		try {
			MqttConnectionParams.validClientID("");
			fail("Must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		try {
			MqttConnectionParams.validClientID("ABCDEFGHIJKLMNOPQRSTUVWX");
			fail("Must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
	}
	
	static protected void validClientID(String clientID) {
		if (clientID == null || clientID.length() <= 0) {
			throw new IllegalArgumentException("No client ID!");
		}
		else if (clientID.length() > MqttUtil.MAX_CLIENTID_LENGTH) {
			throw new IllegalArgumentException("Client ID is greater than " + MqttUtil.MAX_CLIENTID_LENGTH + " characters : " + clientID);
		}
	}

//	@Test
//	public void testGetDefaultPersistenceDirectory() {
//		assertEquals(MqttConnectionParams.getDefaultPersistenceDirectory(), System.getProperty("java.io.tmpdir"));
//	}

	@Test
	public void testMqttConnectionParams() {
		MqttConnectionParams params = new MqttConnectionParams();
		assertEquals(params.getServerURI(), "tcp://127.0.0.1:1883");
		assertTrue(params.getClientID().startsWith(System.getProperty("user.name")));
		MqttConnectOptions opts = new MqttConnectOptions();
		assertNotNull(params.getOptions());
		assertEquals(params.isCleanSession(), opts.isCleanSession());
		assertEquals(params.getConnectionTimeout(), opts.getConnectionTimeout());
		assertEquals(params.getKeepAliveInterval(), opts.getKeepAliveInterval());
	}

	@Test
	public void testMqttConnectionParamsString() {
		MqttConnectionParams params = new MqttConnectionParams("localhost");
		assertEquals(params.getServerURI(), "tcp://localhost:1883");
		assertTrue(params.getClientID().startsWith(System.getProperty("user.name")));
		MqttConnectOptions opts = new MqttConnectOptions();
		assertNotNull(params.getOptions());
		assertEquals(params.isCleanSession(), opts.isCleanSession());
		assertEquals(params.getConnectionTimeout(), opts.getConnectionTimeout());
		assertEquals(params.getKeepAliveInterval(), opts.getKeepAliveInterval());
	}

	@Test
	public void testMqttConnectionParamsStringInt() {
		MqttConnectionParams params = new MqttConnectionParams("localhost", 1884);
		assertEquals(params.getServerURI(), "tcp://localhost:1884");
		assertTrue(params.getClientID().startsWith(System.getProperty("user.name")));
		MqttConnectOptions opts = new MqttConnectOptions();
		assertNotNull(params.getOptions());
		assertEquals(params.isCleanSession(), opts.isCleanSession());
		assertEquals(params.getConnectionTimeout(), opts.getConnectionTimeout());
		assertEquals(params.getKeepAliveInterval(), opts.getKeepAliveInterval());
	}

	@Test
	public void testMqttConnectionParamsStringString() {
		MqttConnectionParams params = new MqttConnectionParams("localhost", "clientID");
		assertEquals(params.getServerURI(), "tcp://localhost:1883");
		assertTrue(params.getClientID().startsWith("clientID"));
		MqttConnectOptions opts = new MqttConnectOptions();
		assertNotNull(params.getOptions());
		assertEquals(params.isCleanSession(), opts.isCleanSession());
		assertEquals(params.getConnectionTimeout(), opts.getConnectionTimeout());
		assertEquals(params.getKeepAliveInterval(), opts.getKeepAliveInterval());
	}

	@Test
	public void testMqttConnectionParamsStringIntString() {
		MqttConnectionParams params = new MqttConnectionParams("localhost", 1884, "clientID");
		assertEquals(params.getServerURI(), "tcp://localhost:1884");
		assertTrue(params.getClientID().startsWith("clientID"));
		MqttConnectOptions opts = new MqttConnectOptions();
		assertNotNull(params.getOptions());
		assertEquals(params.isCleanSession(), opts.isCleanSession());
		assertEquals(params.getConnectionTimeout(), opts.getConnectionTimeout());
		assertEquals(params.getKeepAliveInterval(), opts.getKeepAliveInterval());
	}

	@Test
	public void testSetServerURIString() {
		MqttConnectionParams params = new MqttConnectionParams();
		assertEquals(params.getServerURI(), "tcp://127.0.0.1:1883");
		
		params.setServerURI(null);
		assertEquals(params.getServerURI(), "tcp://127.0.0.1:1883");
		
		params.setServerURI("");
		assertEquals(params.getServerURI(), "tcp://127.0.0.1:1883");
		
		params.setServerURI("localhost");
		assertEquals(params.getServerURI(), "tcp://localhost:1883");
		
		params.setServerURI("ssl://localhost:1885");
		assertEquals(params.getServerURI(), "ssl://localhost:1885");
		
		try {
			params.setServerURI("ssh://localhost:1885");
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetServerURIStringInt() {
		MqttConnectionParams params = new MqttConnectionParams();
		assertEquals(params.getServerURI(), "tcp://127.0.0.1:1883");
		
		params.setServerURI(null, 1884);
		assertEquals(params.getServerURI(), "tcp://127.0.0.1:1884");
		
		params.setServerURI("", 1884);
		assertEquals(params.getServerURI(), "tcp://127.0.0.1:1884");
		
		params.setServerURI("localhost", 1884);
		assertEquals(params.getServerURI(), "tcp://localhost:1884");
		
		params.setServerURI("ssl://localhost:1885", 1884);
		assertEquals(params.getServerURI(), "ssl://localhost:1885");
		
		try {
			params.setServerURI("localhost", -1883);
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		try {
			params.setServerURI("localhost", 65536);
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetClientID() {
		MqttConnectionParams params = new MqttConnectionParams();
		assertTrue(params.getClientID().startsWith(System.getProperty("user.name")));
		
		params.setClientID("hoge");
		assertTrue(params.getClientID().equals("hoge"));
		
		params.setClientID(null);
		assertTrue(params.getClientID().startsWith(System.getProperty("user.name")));
		
		params.setClientID("hoge");
		assertTrue(params.getClientID().equals("hoge"));
		
		params.setClientID("");
		assertTrue(params.getClientID().startsWith(System.getProperty("user.name")));
	}

	@Test
	public void testSetCleanSession() {
		MqttConnectionParams params = new MqttConnectionParams();
		MqttConnectOptions opts = new MqttConnectOptions();
		assertEquals(params.isCleanSession(), opts.isCleanSession());
		
		params.setCleanSession(true);
		assertTrue(params.isCleanSession());
		
		params.setCleanSession(false);
		assertFalse(params.isCleanSession());
	}

	@Test
	public void testSetConnectionTimeout() {
		MqttConnectionParams params = new MqttConnectionParams();
		MqttConnectOptions opts = new MqttConnectOptions();
		assertEquals(params.getConnectionTimeout(), opts.getConnectionTimeout());
		
		params.setConnectionTimeout(0);
		assertEquals(params.getConnectionTimeout(), 0);
		
		params.setConnectionTimeout(1000);
		assertEquals(params.getConnectionTimeout(), 1000);
	}

	@Test
	public void testSetKeepAliveInterval() {
		MqttConnectionParams params = new MqttConnectionParams();
		MqttConnectOptions opts = new MqttConnectOptions();
		assertEquals(params.getKeepAliveInterval(), opts.getKeepAliveInterval());
		
		params.setKeepAliveInterval(0);
		assertEquals(params.getKeepAliveInterval(), 0);
		
		params.setKeepAliveInterval(1000);
		assertEquals(params.getKeepAliveInterval(), 1000);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
