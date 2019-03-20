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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
package ssac.aadl.runtime.mqtt.internal;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttBufferedSession;
import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.MqttPublisher;
import ssac.aadl.runtime.mqtt.MqttUtil;
import ssac.aadl.runtime.mqtt.internal.MqttSessionImplTest.MessageChecker;
import ssac.falconseed.mqtt.broker.MoquetteBrokerLauncher;

public class MqttBufferedSessionImplExtraTest
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static final String TestString	= "今日も良い天気～";
	static final byte[] TestPayload	= TestString.getBytes();
	
	static final String TestTopic	= "/MQTTUtility/test/MqttBufferedSessionImplExtraTest/data";

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
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static class PubTestTask implements Runnable
	{
		private final MqttBufferedSession	_orgSession;
		
		public PubTestTask() {
			this(null);
		}
		
		public PubTestTask(MqttBufferedSession orgSession) {
			_orgSession = orgSession;
		}
		
		public void doTask(MqttBufferedSession mqsession) {
			// place holder
		}
		
		public MqttBufferedSession getOriginalSession() {
			return _orgSession;
		}
		
		@Override
		public void run() {
			MqttBufferedSession pubsession = _orgSession;
			try {
				// connect
				if (_orgSession == null) {
					pubsession = MqttUtil.connectBufferedSession();
				}
				
				// do task
				doTask(pubsession);
			}
			finally {
				// disconnect
				if (_orgSession == null && pubsession != null) {
					if (pubsession.isConnected()) {
						pubsession.disconnect();
					}
				}
			}
		}
	}

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------
	
	@Test
	public void testSingleSubscribeGetMessageDifferentSessionWaitLimited() throws Exception
	{
		final MessageChecker msgChecker = new MessageChecker();
		final String topic = TestTopic;
		
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
			session.subscribe(topic, 1);
			assertTrue(session.isConnected());
			
			// Prepare to Get message
			final long lTimeout = 10000L;	// 10sec
			final long stime = System.currentTimeMillis();
			//--- start publish thread
			Runnable pubtask = new PubTestTask() {
				@Override
				public void doTask(MqttBufferedSession mqsession) {
					long waitTime = (lTimeout / 2) - (System.currentTimeMillis() - stime);
					if (waitTime > 0L) {
						try {
							Thread.sleep(waitTime);
						} catch (InterruptedException ex) {
							// ignore
						}
					}
					
					// publish message
					MqttPublisher.publishStringUTF8(mqsession, topic, TestString, 1, false);
				}
			};
			Thread pubthread = new Thread(pubtask);
			pubthread.start();
			
			// Get message and wait
			MqttArrivedMessage mqmsg = session.getMessage(lTimeout, true);	// remove from queue
			final long etime = System.currentTimeMillis();
			
			// wait thread
			pubthread.join();
			
			// check message
			assertNotNull(mqmsg);
			System.out.println("@@@ MqttBufferedSessionImplExtraTest#testSingleSubscribeGetMessageDifferentSessionWaitLimited() : Subscribe time = "
								+ (etime-stime) + " msec.");
			assertEquals(msgChecker.subTopic  , topic);
			assertTrue(Arrays.equals(msgChecker.subPayload, TestString.getBytes("UTF-8")));
			assertEquals(msgChecker.subQos    , 1);
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
	public void testSingleSubscribeGetMessageSameSessionWaitLimited() throws Exception
	{
		final MessageChecker msgChecker = new MessageChecker();
		final String topic = TestTopic;
		
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
			session.subscribe(topic, 1);
			assertTrue(session.isConnected());
			
			// Prepare to Get message
			final long lTimeout = 10000L;	// 10sec
			final long stime = System.currentTimeMillis();
			//--- start publish thread
			Runnable pubtask = new PubTestTask(session) {
				@Override
				public void doTask(MqttBufferedSession mqsession) {
					long waitTime = (lTimeout / 2) - (System.currentTimeMillis() - stime);
					if (waitTime > 0L) {
						try {
							Thread.sleep(waitTime);
						} catch (InterruptedException ex) {
							// ignore
						}
					}
					
					// publish message
					MqttPublisher.publishStringUTF8(mqsession, topic, TestString, 1, false);
				}
			};
			Thread pubthread = new Thread(pubtask);
			pubthread.start();
			
			// Get message and wait
			MqttArrivedMessage mqmsg = session.getMessage(lTimeout, true);	// remove from queue
			final long etime = System.currentTimeMillis();
			
			// wait thread
			pubthread.join();
			
			// check message
			assertNotNull(mqmsg);
			System.out.println("@@@ MqttBufferedSessionImplExtraTest#testSingleSubscribeGetMessageSameSessionWaitLimited() : Subscribe time = "
								+ (etime-stime) + " msec.");
			assertEquals(msgChecker.subTopic  , topic);
			assertTrue(Arrays.equals(msgChecker.subPayload, TestString.getBytes("UTF-8")));
			assertEquals(msgChecker.subQos    , 1);
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
	public void testSingleSubscribeGetMessageSameThreadWaitLimited() throws Exception
	{
		final MessageChecker msgChecker = new MessageChecker();
		final String topic = TestTopic;

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
			session.subscribe(topic, 1);
			assertTrue(session.isConnected());

			// Prepare to Get message
			final long lTimeout = 10000L;	// 10sec
			final long stime = System.currentTimeMillis();
			//--- start publish thread
			//--- publish
			MqttPublisher.publishStringUTF8(session, topic, TestString, 1, false);
			// Get message and wait
			MqttArrivedMessage mqmsg = session.getMessage(lTimeout, true);	// remove from queue
			final long etime = System.currentTimeMillis();

			// check message
			assertNotNull(mqmsg);
			System.out.println("@@@ MqttBufferedSessionImplExtraTest#testSingleSubscribeGetMessageSameThreadWaitLimited() : Subscribe time = "
					+ (etime-stime) + " msec.");
			assertEquals(msgChecker.subTopic  , topic);
			assertTrue(Arrays.equals(msgChecker.subPayload, TestString.getBytes("UTF-8")));
			assertEquals(msgChecker.subQos    , 1);
		}
		finally {
			// disconnect
			if (session.isConnected()) {
				session.unsubscribe(topic);
				session.disconnect();
			}
		}
	}
}
