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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ssac.aadl.runtime.mqtt.event.MqttEventHandler;
import ssac.aadl.runtime.mqtt.internal.MqttManagedSessionImpl;
import ssac.aadl.runtime.mqtt.internal.MqttSessionImpl;
import ssac.falconseed.mqtt.broker.util.MoquetteServer;

public class MqttUtilTest
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static protected MoquetteServer _server = null;

	//------------------------------------------------------------
	// Setup
	//------------------------------------------------------------

	@BeforeClass
	static public void doBeforeClass() throws Exception
	{
		System.out.println("[Test] start Moquette Broker server...");
		_server = new MoquetteServer();
		_server.startServer();
	}

	@AfterClass
	static public void doAfterClass() throws Exception
	{
		System.out.println("[Test] Moquette Broker server shutdowning...");
		if (_server != null) {
			_server.stopServer();
			_server = null;
		}
		System.out.println("[Test] Moquette Broker server shutdowned.");
	}

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	@Test
	public void testGetMqttUtilJarDir() {
		File dir = MqttUtil.getMqttUtilJarDir();
		assertEquals(new File("").getAbsoluteFile(), dir);
	}

	@Test
	public void testIsNull() {
		Object v1 = new Object();
		String v2 = "hoge";
		
		assertTrue(MqttUtil.isNull(null));
		assertFalse(MqttUtil.isNull(v1));
		assertFalse(MqttUtil.isNull(v2));
	}

	@Test
	public void testGenerateClientID() {
		String userName = System.getProperty("user.name");
		if (userName.length() > MqttUtil.MAX_CLIENTID_LENGTH) {
			userName = userName.substring(0, MqttUtil.MAX_CLIENTID_LENGTH);
		}
		String clientID = MqttUtil.generateClientID();
		assertTrue(clientID.startsWith(userName));
		assertTrue(clientID.length() <= MqttUtil.MAX_CLIENTID_LENGTH);
	}

	@Test
	public void testGenerateClientIDString() {
//		// check
//		{
//			double sv1 = Math.pow(10.0, 1.0) - 1.0;
//			double sv2 = Math.pow(10.0, 18.0) - 1.0;
//			BigDecimal bv1 = BigDecimal.valueOf(10L).pow(1).subtract(BigDecimal.ONE);
//			BigDecimal bv2 = BigDecimal.valueOf(10L).pow(18).subtract(BigDecimal.ONE);
//			
//			double rsv1 = Math.random() * sv1;
//			double rsv2 = Math.random() * sv2;
//			
//			BigDecimal rbv1 = bv1.multiply(BigDecimal.valueOf(Math.random()));
//			BigDecimal rbv2 = bv2.multiply(BigDecimal.valueOf(Math.random()));
//			
//			long lsv1 = (long)rsv1;
//			long lsv2 = (long)rsv2;
//			long lbv1 = rbv1.longValue();
//			long lbv2 = rbv2.longValue();
//			
//			long tm = System.currentTimeMillis();
//
//			double[] rs = new double[100];
//			long[] vals = new long[100];
//			for (int i = 0; i < 100; i++) {
//				rs[i] = Math.random();
//				vals[i] = Math.abs(bv2.multiply(BigDecimal.valueOf(rs[i])).longValue());
//			}
//			for (int i = 0; i < (100-1); i++) {
//				long val = vals[i];
//				for (int j = i+1; j < 100; j++) {
//					assertFalse(val == vals[j]);
//				}
//			}
//			
//			
//			assertTrue(true);
//		}
		
		
		
		
		
		String userName = System.getProperty("user.name");
		if (userName.length() > MqttUtil.MAX_CLIENTID_LENGTH) {
			userName = userName.substring(0, MqttUtil.MAX_CLIENTID_LENGTH);
		}
		String clientID;
		
		// null
		clientID = MqttUtil.generateClientID(null);
		assertTrue(clientID.startsWith(userName));
		assertTrue(clientID.length() <= MqttUtil.MAX_CLIENTID_LENGTH);
		
		// empty
		clientID = MqttUtil.generateClientID("");
		assertTrue(clientID.startsWith(userName));
		assertTrue(clientID.length() <= MqttUtil.MAX_CLIENTID_LENGTH);
		
		// over MAX_CLIENT_LENGTH
		final String overLen = "ABCDEFGHIJKLMNOPQRSTUVW_";
		assertTrue(overLen.length() > MqttUtil.MAX_CLIENTID_LENGTH);
		try {
			clientID = MqttUtil.generateClientID(overLen);
			fail("Must be throw IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		// just MAX_CLIENT_LENGTH
		final String justLen = "ABCDEFGHIJKLMNOPQRSTUVW";
		assertTrue(justLen.length() == MqttUtil.MAX_CLIENTID_LENGTH);
		clientID = MqttUtil.generateClientID(justLen);
		assertEquals(justLen, clientID);
		
		// under MAX_CLIENT_LENGTH
		final String underLen = "ABCDEFGHIJKLMNOPQRSTUV";
		assertTrue(underLen.length() < MqttUtil.MAX_CLIENTID_LENGTH);
		clientID = MqttUtil.generateClientID(underLen);
		assertTrue(clientID.startsWith(underLen));
		assertTrue(clientID.length() <= MqttUtil.MAX_CLIENTID_LENGTH);
		
		// check generation
		final String prefix = "TestClientID";
		final int testlen = 10;
		String[] cids = new String[testlen];
		//--- generate
		for (int i = 0; i < testlen; i++) {
			cids[i] = MqttUtil.generateClientID(prefix);
		}
		//--- check
		for (int i = 0; i < testlen; i++) {
			String clientID1 = cids[i];
			assertTrue(clientID1.length() <= MqttUtil.MAX_CLIENTID_LENGTH);
			assertTrue(clientID1.startsWith(prefix));
			for (int j = i+1; j < testlen; j++) {
				String clientID2 = cids[j];
				assertFalse(clientID1.equals(clientID2));
			}
		}
	}

	@Test
	public void testGetServerURI() {
		MqttConnectionParams params = new MqttConnectionParams();
		MqttSessionImpl session = new MqttSessionImpl(params);
		assertEquals(params.getServerURI(), MqttUtil.getServerURI(session));
	}

	@Test
	public void testGetClientID() {
		MqttConnectionParams params = new MqttConnectionParams();
		MqttSessionImpl session = new MqttSessionImpl(params);
		assertEquals(params.getClientID(), MqttUtil.getClientID(session));
	}

//	@Test
//	public void testIsConnectionLost() throws Exception
//	{
//		MqttConnectionParams params = new MqttConnectionParams();
//		MqttSessionImpl session = new MqttSessionImpl(params);
//		final CountDownLatch doneDisconnect = new CountDownLatch(1);
//		session.setEventHandler(new MqttEventHandler() {
//			@Override
//			public void messageArrived(MqttSession session, MqttArrivedMessage message) throws Exception {
//			}
//			
//			@Override
//			public void disconnected(MqttSession session) {
//				doneDisconnect.countDown();
//			}
//			
//			@Override
//			public void connectionLost(MqttSession session, Throwable cause) {
//				doneDisconnect.countDown();
//			}
//		});
//		assertFalse(session.isConnectionLost());
//		assertEquals(session.isConnectionLost(), MqttUtil.isConnectionLost(session));
//		assertNull(session.getConnectionLostCause());
//		assertEquals(session.getConnectionLostCause(), MqttUtil.getConnectionLostCause(session));
//		
//		// connect
//		session.connect();
//		//--- close server
//		_server.stopServer();
//		
//		// wait disconnect
//		try {
//			doneDisconnect.await();
//		} catch (InterruptedException ex) {
//			System.err.println("Cought Interrupted exception : " + ex);
//		}
//
//		assertTrue(session.isConnectionLost());
//		assertEquals(session.isConnectionLost(), MqttUtil.isConnectionLost(session));
//		assertNotNull(session.getConnectionLostCause());
//		assertEquals(session.getConnectionLostCause(), MqttUtil.getConnectionLostCause(session));
//		
//		// restart server
//		_server.startServer();
//	}
//
//	@Test
//	public void testConnection() {
//		MqttConnectionParams params = new MqttConnectionParams();
//		MqttSessionImpl session = new MqttSessionImpl(params);
//		assertFalse(session.isConnected());
//		
//		// connect
//		session.connect();
//		
//		assertTrue(session.isConnected());
//		assertEquals(session.isConnected(), MqttUtil.isConnected(session));
//		
//		// disconnect
//		MqttUtil.disconnect(session);
//		assertFalse(session.isConnected());
//		assertEquals(session.isConnected(), MqttUtil.isConnected(session));
//		
//		// multi connect
//		MqttConnectionParams params1 = new MqttConnectionParams(null, "client1");
//		MqttConnectionParams params2 = new MqttConnectionParams(null, "client2");
//		MqttSessionImpl session1 = new MqttSessionImpl(params1);
//		MqttSessionImpl session2 = new MqttSessionImpl(params2);
//		assertFalse(session1.isConnected());
//		assertFalse(session2.isConnected());
//		//--- connect
//		session1.connect();
//		session2.connect();
//		boolean s1 = session1.isConnected();
//		boolean s2 = session2.isConnected();
//		assertTrue(session1.isConnected());
//		assertTrue(session2.isConnected());
//		//--- disconnect
//		session2.disconnect();
//		session1.disconnect();
//		assertFalse(session1.isConnected());
//		assertFalse(session2.isConnected());
//	}
//
//	@Test
//	public void testConnectBufferedSession() {
//		MqttConnectionParams params = new MqttConnectionParams();
//		
//		// connection
//		MqttBufferedSession session = MqttUtil.connectBufferedSession();
//		assertTrue(session.isConnected());
//		assertEquals(params.getServerURI(), session.getServerURI());
//		//--- disconnect
//		session.disconnect();
//		assertFalse(session.isConnected());
//	}
//
//	@Test
//	public void testConnectBufferedSessionString() {
//		MqttConnectionParams params = new MqttConnectionParams("localhost");
//		
//		// connection
//		MqttBufferedSession session = MqttUtil.connectBufferedSession("localhost");
//		assertTrue(session.isConnected());
//		assertEquals(params.getServerURI(), session.getServerURI());
//		//--- disconnect
//		session.disconnect();
//		assertFalse(session.isConnected());
//	}
//
//	@Test
//	public void testConnectBufferedSessionStringString() {
//		String clientID = MqttUtil.generateClientID("test");
//		MqttConnectionParams params = new MqttConnectionParams("localhost", clientID);
//		
//		// connection
//		MqttBufferedSession session = MqttUtil.connectBufferedSession("localhost", clientID);
//		assertTrue(session.isConnected());
//		assertEquals(params.getServerURI(), session.getServerURI());
//		assertEquals(params.getClientID(), session.getClientID());
//		//--- disconnect
//		session.disconnect();
//		assertFalse(session.isConnected());
//	}

	@Test
	public void testConnectBufferedSessionMqttConnectionParams() {
		MqttConnectionParams params;
		MqttBufferedSession session;
		
		// null
		try {
			MqttUtil.connectBufferedSession((MqttConnectionParams)null);
			fail("Must be throw NullPointerException");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// no server
		params = new MqttConnectionParams("127.0.0.1", 1884);
		try {
			MqttUtil.connectBufferedSession(params);
			fail("Must be throw MqttRuntimeException");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(true);
		}
		
		// default
		System.setProperty(MqttUtil.MQTT_CLIENT_TRACE, "true");
		params = new MqttConnectionParams();
		session = MqttUtil.connectBufferedSession(params);
		assertEquals(params.getServerURI(), session.getServerURI());
		assertEquals(params.getClientID(), session.getClientID());
		assertTrue(session.isConnected());
		//--- disconnect
		session.disconnect();
		assertFalse(session.isConnected());
	}

	@Test
	public void testDisconnectAllSessions() {
//		final CountDownLatch doneDisconn = new CountDownLatch(3);
//		MqttEventHandler handler = new MqttEventHandler() {
//			@Override
//			public void messageArrived(MqttSession session, MqttArrivedMessage message) throws Exception {
//			}
//			
//			@Override
//			public void disconnected(MqttSession session) {
//				doneDisconn.countDown();
//			}
//			
//			@Override
//			public void connectionLost(MqttSession session, Throwable cause) {
//			}
//		};
//		
//		MqttManagedSessionImpl session1 = new MqttManagedSessionImpl(new MqttConnectionParams(null, MqttUtil.generateClientID("test1")));
//		MqttManagedSessionImpl session2 = new MqttManagedSessionImpl(new MqttConnectionParams(null, MqttUtil.generateClientID("test2")));
//		MqttManagedSessionImpl session3 = new MqttManagedSessionImpl(new MqttConnectionParams(null, MqttUtil.generateClientID("test3")));
//		assertFalse(session1.isConnected());
//		assertFalse(session2.isConnected());
//		assertFalse(session3.isConnected());
//		
//		// connect
//		session1.connect();
//		session2.connect();
//		session3.connect();
//		//--- check
//		assertTrue(session1.isConnected());
//		assertTrue(session2.isConnected());
//		assertTrue(session3.isConnected());
//		
//		// disconnect all
//		MqttUtil.disconnectAllSessions();
//		//--- check
//		assertFalse(session1.isConnected());
//		assertFalse(session2.isConnected());
//		assertFalse(session3.isConnected());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
