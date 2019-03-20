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
package ssac.aadl.runtime.mqtt.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ssac.aadl.runtime.mqtt.MqDeliveryToken;
import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.MqttRuntimeException;
import ssac.aadl.runtime.mqtt.MqttSession;
import ssac.aadl.runtime.mqtt.event.MqttEventHandler;
import ssac.aadl.runtime.mqtt.internal.MqttSessionImplTest.MessageChecker;
import ssac.falconseed.mqtt.broker.MoquetteBrokerLauncher;

public class MqttBufferedSessionImplBasicTest
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

	@BeforeClass
	static public void doBeforeClass() throws Exception
	{
		System.out.println("[Test] start Moquette Broker server...");
		MoquetteBrokerLauncher.startBrokerInprocess();
	}

	@AfterClass
	static public void doAfterClass() throws Exception
	{
		System.out.println("[Test] Moquette Broker server shutdowning...");
		MoquetteBrokerLauncher.stopBrokerInprocess();
	}

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	@Test
	public void testValidTopicFilter() {
		try {
			MqttBufferedSessionImpl.validTopicFilter(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		try {
			MqttBufferedSessionImpl.validTopicFilter("");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}

		MqttBufferedSessionImpl.validTopicFilter("h");
	}

	@Test
	public void testMqttBufferedSessionImpl() {
		try {
			new MqttBufferedSessionImpl(null);
			fail("NullPointerException is expected.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		MqttConnectionParams params = new MqttConnectionParams();
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(params);
		assertFalse(session.isConnected());
		assertEquals(session.getServerURI(), params.getServerURI());
		assertEquals(session.getClientID(), params.getClientID());
		
		params = new MqttConnectionParams("localhost:1885", "hogehogepoo");
		session = new MqttBufferedSessionImpl(params);
		assertFalse(session.isConnected());
		assertEquals(session.getServerURI(), params.getServerURI());
		assertEquals(session.getClientID(), params.getClientID());
	}

	@Test
	public void testGetParameters() {
		MqttConnectionParams params = new MqttConnectionParams();
		params.setServerURI("localhost:2083");
		params.setClientID("hogehogepoo");
		params.setCleanSession(true);
		params.setConnectionTimeout(1000);
		params.setKeepAliveInterval(2000);
		assertEquals(params.getServerURI(), "tcp://localhost:2083");
		assertEquals(params.getClientID(), "hogehogepoo");
		assertTrue(params.isCleanSession());
		assertEquals(params.getConnectionTimeout(), 1000);
		assertEquals(params.getKeepAliveInterval(), 2000);
		
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(params);
		assertEquals(session.getServerURI(), params.getServerURI());
		assertEquals(session.getClientID(), params.getClientID());
		assertEquals(session.isCleanSession(), params.isCleanSession());
		assertEquals(session.getConnectionTimeout(), params.getConnectionTimeout());
		assertEquals(session.getKeepAliveInterval(), params.getKeepAliveInterval());
	}

	@Test
	public void testConnection() {
		MqttBufferedSessionImpl session = null;
		MqttBufferedSessionImpl session1 = null;
		MqttBufferedSessionImpl session2 = null;
		try {
			MqttConnectionParams params = new MqttConnectionParams();
			session = new MqttBufferedSessionImpl(params);
			assertFalse(session.isConnected());
			assertFalse(session.isConnectionLost());
			assertNull(session.getConnectionLostCause());
			try {
				session.ensureConnected(null);
				fail("MqttRuntimeException is expected.");
			} catch (MqttRuntimeException ex) {
				assertTrue(true);
			}

			// connect
			session.connect();
			assertTrue(session.isConnected());
			assertFalse(session.isConnectionLost());
			assertNull(session.getConnectionLostCause());
			session.ensureConnected(null);

			// disconnect
			session.disconnect();
			assertFalse(session.isConnected());
			assertFalse(session.isConnectionLost());
			assertNull(session.getConnectionLostCause());
			try {
				session.ensureConnected(null);
				fail("MqttRuntimeException is expected.");
			} catch (MqttRuntimeException ex) {
				assertTrue(true);
			}

			// multi connect
			MqttConnectionParams params1 = new MqttConnectionParams(null, "client1");
			MqttConnectionParams params2 = new MqttConnectionParams(null, "client2");
			session1 = new MqttBufferedSessionImpl(params1);
			session2 = new MqttBufferedSessionImpl(params2);
			assertFalse(session1.isConnected());
			assertFalse(session1.isConnectionLost());
			assertNull(session1.getConnectionLostCause());
			assertFalse(session2.isConnected());
			assertFalse(session2.isConnectionLost());
			assertNull(session2.getConnectionLostCause());
			//--- connect
			session1.connect();
			session2.connect();
			assertTrue(session1.isConnected());
			assertFalse(session1.isConnectionLost());
			assertNull(session1.getConnectionLostCause());
			assertTrue(session2.isConnected());
			assertFalse(session2.isConnectionLost());
			assertNull(session2.getConnectionLostCause());
			//--- disconnect
			session2.disconnect();
			session1.disconnect();
			assertFalse(session1.isConnected());
			assertFalse(session1.isConnectionLost());
			assertNull(session1.getConnectionLostCause());
			assertFalse(session2.isConnected());
			assertFalse(session2.isConnectionLost());
			assertNull(session2.getConnectionLostCause());
		}
		finally {
			if (session != null && session.isConnected())
				session.disconnect();
			if (session1 != null && session1.isConnected())
				session1.disconnect();
			if (session2 != null && session2.isConnected())
				session2.disconnect();
		}
	}

	@Test
	public void testIsConnectionLost() throws Exception
	{
		MqttConnectionParams params = new MqttConnectionParams();
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(params);
		assertFalse(session.isConnected());
		assertFalse(session.isConnectionLost());
		assertNull(session.getConnectionLostCause());
		try {
			session.ensureConnected(null);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {
			assertTrue(true);
		}

		try {
			// connect
			session.connect();
			assertTrue(session.isConnected());
			assertFalse(session.isConnectionLost());
			assertNull(session.getConnectionLostCause());
			session.ensureConnected(null);
			//--- close server
			MoquetteBrokerLauncher.stopBrokerInprocess();
			try {
				Thread.sleep(1000);	// サーバー終了まで1秒待機
			} catch (Throwable ignoreEx) {}
			assertFalse(session.isConnected());
			assertTrue(session.isConnectionLost());
			assertNotNull(session.getConnectionLostCause());
			try {
				session.ensureConnected(null);
				fail("MqttRuntimeException is expected.");
			} catch (MqttRuntimeException ex) {
				assertTrue(true);
			}
		}
		finally {
			// disconnect
			if (session.isConnected()) {
				session.disconnect();
			}
		}
		
		// restart server
		try {
			Thread.sleep(1000);	// サーバー起動まで1秒待機
		} catch (Throwable ignoreEx) {}
		MoquetteBrokerLauncher.startBrokerInprocess();
	}

	@Test
	public void testSetEventHandler() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertNull(session.getEventHandler());
		
		session.setEventHandler(new MqttEventHandler() {
			@Override
			public void messageArrived(MqttSession session, MqttArrivedMessage message) throws Exception {
			}
			
			@Override
			public void disconnected(MqttSession session) {
			}
			
			@Override
			public void connectionLost(MqttSession session, Throwable cause) {
			}
		});
		assertNotNull(session.getEventHandler());
		
		session.setEventHandler(null);
		assertNull(session.getEventHandler());
	}

	@Test
	public void testAsyncPublish() {
		final MessageChecker msgChecker = new MessageChecker();
		
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams()){
			@Override
			protected void onMessageArrived(MqttArrivedMessage message) throws Exception {
				msgChecker.setSubMessage(message);
				
				super.onMessageArrived(message);
			}
		};
		
		// publish data set
		msgChecker.setPubMessage("/mqtt/test/topic1", "Test Publish message.", 1, false);
		
		// check QoS
		try {
			session.asyncPublish(msgChecker.pubTopic, msgChecker.pubPayload, -1, false);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			session.asyncPublish(msgChecker.pubTopic, msgChecker.pubPayload, 3, false);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		
		// no connection
		try {
			session.asyncPublish(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) { assertTrue(true); }

		try {
			// connect
			session.connect();

			// subscribe
			session.subscribe("/mqtt/test/+");
			assertTrue(session.isConnected());

			// pattern 1
			msgChecker.setPubMessage("/mqtt/test/topic1", "Test Publish message 1.", 1, false);
			MqDeliveryToken token = session.asyncPublish(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			try {
				token.waitForCompletion();
			} catch (Throwable ex) {
				fail("Caught exception : " + ex);
			}
			assertTrue(token.isCompleted());
			msgChecker.assertMessageArrived(1000L);
			msgChecker.assertCompleted();

			// pattern 2
			msgChecker.setPubMessage("/mqtt/test/topic2", "Test Publish message 2.", 0, false);
			token = session.asyncPublish(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			try {
				token.waitForCompletion();
			} catch (Throwable ex) {
				fail("Caught exception : " + ex);
			}
			assertTrue(token.isCompleted());
			msgChecker.assertMessageArrived(1000L);
			msgChecker.assertCompleted();
		}
		finally {
			// disconnect
			if (session.isConnected()) {
				session.unsubscribe("/mqtt/test/+");
				session.disconnect();
			}
		}
	}

	@Test
	public void testPublishAndWait() {
		final MessageChecker msgChecker = new MessageChecker();
		
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams()){
			@Override
			protected void onMessageArrived(MqttArrivedMessage message) throws Exception {
				msgChecker.setSubMessage(message);
				
				super.onMessageArrived(message);
			}
		};
		
		// publish data set
		msgChecker.setPubMessage("/mqtt/test/topic1", "Test Publish message.", 1, false);
		
		// check QoS
		try {
			session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, -1, false);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, 3, false);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		
		// no connection
		try {
			session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) { assertTrue(true); }

		try {
			// connect
			session.connect();

			// subscribe
			session.subscribe("/mqtt/test/+");
			assertTrue(session.isConnected());

			// pattern 1
			msgChecker.setPubMessage("/mqtt/test/topic1", "Test Publish message 1.", 1, false);
			session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			msgChecker.assertMessageArrived(1000L);
			msgChecker.assertCompleted();

			// pattern 2
			msgChecker.setPubMessage("/mqtt/test/topic2", "Test Publish message 2.", 0, false);
			session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			msgChecker.assertMessageArrived(1000L);
			msgChecker.assertCompleted();
		}
		finally {
			// disconnect
			if (session.isConnected()) {
				session.unsubscribe("/mqtt/test/+");
				session.disconnect();
			}
		}
	}

	@Test
	public void testSubscribeError() {
		final String[] topics = {
			"/mqtt/test/topic1",
			"/mqtt/test/topic2",
			"/mqtt/test/topic3",
		};
		final int[] qoss = {
			0, 1, 2,
		};
		final int[] errQoss = {
			0, 3, 2,
		};
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		
		// no connection
		try {
			session.subscribe("topic");
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {
			assertTrue(true);
		}
		try {
			session.subscribe("topic", 0);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {
			assertTrue(true);
		}
		try {
			session.subscribe(topics);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {
			assertTrue(true);
		}
		try {
			session.subscribe(topics, qoss);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {
			assertTrue(true);
		}
		
		// qos illegal
		try {
			session.subscribe("topic", 3);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			session.subscribe(topics, errQoss);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testUnsubscribeError() {
		final String[] topics = {
				"/mqtt/test/topic1",
				"/mqtt/test/topic2",
				"/mqtt/test/topic3",
		};
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		
		// no connection
		try {
			session.unsubscribe("topic");
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {
			assertTrue(true);
		}
		try {
			session.unsubscribe(topics);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testSingleSubscribe() {
		final MessageChecker msgChecker = new MessageChecker();
		final String topic = "/mqtt/test/SingleSubscribe/topic1";
		
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams()){
			@Override
			protected void onMessageArrived(MqttArrivedMessage message) throws Exception {
				msgChecker.setSubMessage(message);
				
				super.onMessageArrived(message);
			}
		};
		assertTrue(session.isCleanSession());

		try {
			// connect
			session.connect();

			// check
			msgChecker.setPubMessage(topic, "Test Publish message 1.", 1, false);
			session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			msgChecker.assertNoMessageArrived(1000L);

			// subscribe
			session.subscribe(topic, 0);
			assertTrue(session.isConnected());

			// pattern 1
			msgChecker.setPubMessage(topic, "Test Publish message 1.", 1, false);
			session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			msgChecker.assertMessageArrived(1000L);
			msgChecker.assertCompleted();

			// unsubscribe
			session.unsubscribe(topic);

			// pattern 2
			msgChecker.setPubMessage(topic, "Test Publish message 2.", 1, false);
			session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
			msgChecker.assertNoMessageArrived(1000L);
		}
		finally {
			// disconnect
			if (session.isConnected()) {
				session.unsubscribe(topic);
				session.disconnect();
			}
		}
	}
	
	@Test
	public void testMultiSubscribe() {
		final MessageChecker msgChecker = new MessageChecker();
		final String[] topics = {
				"/mqtt/test/MultiSubscribe/topic1",
				"/mqtt/test/MultiSubscribe/topic2",
				"/mqtt/test/MultiSubscribe/topic3",
		};
		final boolean[] unsubs = {
				true,
				false,
				true,
		};
		final String[] msgs = {
				"Test Publish message 1.",
				"Test Publish message 2.",
				"Test Publish message 3.",
		};
		final int[] qoss = {
				0, 1, 0,
		};
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < topics.length; i++) {
			if (unsubs[i]) {
				list.add(topics[i]);
			}
		}
		final String[] unsubTopics = list.toArray(new String[list.size()]);
		
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams()){
			@Override
			protected void onMessageArrived(MqttArrivedMessage message) throws Exception {
				msgChecker.setSubMessage(message);
				
				super.onMessageArrived(message);
			}
		};

		try {
			// connect
			session.connect();
			//--- check
			assertTrue(session.isConnected());
			for (int i = 0; i < topics.length; i++) {
				msgChecker.setPubMessage(topics[i], msgs[i], qoss[i], false);
				session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
				msgChecker.assertNoMessageArrived(1000L);
			}

			// subscribe
			session.subscribe(topics, qoss);
			assertTrue(session.isConnected());

			// all check
			for (int i = 0; i < topics.length; i++) {
				msgChecker.setPubMessage(topics[i], msgs[i], qoss[i], false);
				session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
				msgChecker.assertMessageArrived(1000L);
				msgChecker.assertCompleted();
			}

			// unsubscribe
			session.unsubscribe(unsubTopics);

			// all check
			for (int i = 0; i < topics.length; i++) {
				msgChecker.setPubMessage(topics[i], msgs[i], qoss[i], false);
				session.publishAndWait(msgChecker.pubTopic, msgChecker.pubPayload, msgChecker.pubQos, msgChecker.pubRetained);
				if (unsubs[i]) {
					// unsubscribed topic
					msgChecker.assertNoMessageArrived(1000L);
				} else {
					// subscribed topic
					msgChecker.assertMessageArrived(1000L);
					msgChecker.assertCompleted();
				}
			}
		}
		finally {
			// disconnect
			if (session.isConnected()) {
				session.unsubscribe(topics);
				session.disconnect();
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
