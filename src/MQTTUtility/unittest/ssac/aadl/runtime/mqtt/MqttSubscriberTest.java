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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ssac.aadl.runtime.mqtt.internal.MqttBufferedSessionImpl;
import ssac.aadl.runtime.mqtt.internal.MqttBufferedSessionImplCoreTest;

public class MqttSubscriberTest
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String testTopic1 = "/mqtt/test/topic/1";
	static private final String testTopic2 = "/mqtt/test/topic/2";
	
	static private final String[] testTopicFilterArray = {
		"/mqtt/test/topic/1",
		"/mqtt/test/topic/2",
		"/mqtt/test/topic/3",
		"/mqtt/subscribe/topic/to4",
		"/mqtt/subscribe/topic/to5",
	};
	static private final String[] includeNullTopicFilterArray = {
		"/mqtt/test/topic/1",
		null,
		"/mqtt/test/topic/3",
		null,
		"/mqtt/subscribe/topic/to5",
	};
	static private final String[] includeEmptyTopicFilterArray = {
		"/mqtt/test/topic/1",
		"",
		"/mqtt/test/topic/3",
		"",
		"/mqtt/subscribe/topic/to5",
	};
	
	static private final List<String> testTopicFilters = new ArrayList<String>(Arrays.asList(testTopicFilterArray));
	static private final List<String> includeNullTopicFilters = new ArrayList<String>(Arrays.asList(includeNullTopicFilterArray));
	static private final List<String> includeEmptyTopicFilters = new ArrayList<String>(Arrays.asList(includeEmptyTopicFilterArray));
	static private final String TestStringData1 = "テスト・ストリングデータ・１";
	static private final String TestStringData2 = "テスト・ストリングデータ・２";
	
	static private final String baseOutputPath  = "testdata/unittest/output/MqttSubscriberTest_";

	static private final String fnameOutputEUC   = baseOutputPath + "EUC-JP.txt";
	static private final String fnameOutputUTF8  = baseOutputPath + "UTF-8.txt";

	static private byte[] getStringBytes(String text, String encoding) {
		if (encoding != null && encoding.length() > 0) {
			try {
				return text.getBytes(encoding);
			} catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
		} else {
			return text.getBytes();
		}
	}
	
	static private byte[] loadFile(String filename) {
		File file = new File(filename);
		byte[] data = new byte[(int)file.length()];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(data);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Throwable ex) {}
			}
		}
		return data;
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Test
	public void testSubscribeMqttSessionString() {
		SubscribeTestSession session = new SubscribeTestSession();
		
		// check null
		try {
			MqttSubscriber.subscribe(null, testTopic1);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.subscribe(session, (String)null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}

		// check call
		session.clear();
		assertTrue(session.subTopics.isEmpty());
		assertTrue(session.subQos.isEmpty());
		assertTrue(session.unsubTopics.isEmpty());
		MqttSubscriber.subscribe(session, testTopic1);
		session.checkSubscribe(testTopic1, 1);
	}

	@Test
	public void testSubscribeMqttSessionStringInt() {
		SubscribeTestSession session = new SubscribeTestSession();
		
		// check null
		try {
			MqttSubscriber.subscribe(null, testTopic1, 0);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.subscribe(session, (String)null, 0);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}

		// check call
		session.clear();
		assertTrue(session.subTopics.isEmpty());
		assertTrue(session.subQos.isEmpty());
		assertTrue(session.unsubTopics.isEmpty());
		MqttSubscriber.subscribe(session, testTopic1, 2);
		session.checkSubscribe(testTopic1, 2);
	}

	@Test
	public void testSubscribeMqttSessionListOfString() {
		SubscribeTestSession session = new SubscribeTestSession();
		
		// check null
		try {
			MqttSubscriber.subscribe(null, testTopicFilters);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.subscribe(session, (List<String>)null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.subscribe(session, includeNullTopicFilters);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check topic empty
		try {
			MqttSubscriber.subscribe(session, includeEmptyTopicFilters);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}

		// check call
		session.clear();
		assertTrue(session.subTopics.isEmpty());
		assertTrue(session.subQos.isEmpty());
		assertTrue(session.unsubTopics.isEmpty());
		MqttSubscriber.subscribe(session, testTopicFilters);
		session.checkSubscribe(testTopicFilters, 1);
	}

	@Test
	public void testSubscribeMqttSessionListOfStringInt() {
		SubscribeTestSession session = new SubscribeTestSession();
		
		// check null
		try {
			MqttSubscriber.subscribe(null, testTopicFilters, 0);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.subscribe(session, (List<String>)null, 0);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.subscribe(session, includeNullTopicFilters, 0);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check topic empty
		try {
			MqttSubscriber.subscribe(session, includeEmptyTopicFilters, 0);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}

		// check call
		session.clear();
		assertTrue(session.subTopics.isEmpty());
		assertTrue(session.subQos.isEmpty());
		assertTrue(session.unsubTopics.isEmpty());
		MqttSubscriber.subscribe(session, testTopicFilters, 2);
		session.checkSubscribe(testTopicFilters, 2);
	}

	@Test
	public void testUnsubscribeMqttSessionString() {
		SubscribeTestSession session = new SubscribeTestSession();
		
		// check null
		try {
			MqttSubscriber.unsubscribe(null, testTopic1);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.unsubscribe(session, (String)null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}

		// check call
		session.clear();
		assertTrue(session.subTopics.isEmpty());
		assertTrue(session.subQos.isEmpty());
		assertTrue(session.unsubTopics.isEmpty());
		MqttSubscriber.unsubscribe(session, testTopic1);
		session.checkUnsubscribe(testTopic1);
	}

	@Test
	public void testUnsubscribeMqttSessionListOfString() {
		SubscribeTestSession session = new SubscribeTestSession();
		
		// check null
		try {
			MqttSubscriber.unsubscribe(null, testTopicFilters);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.unsubscribe(session, (List<String>)null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			MqttSubscriber.unsubscribe(session, includeNullTopicFilters);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check topic empty
		try {
			MqttSubscriber.unsubscribe(session, includeEmptyTopicFilters);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}

		// check call
		session.clear();
		assertTrue(session.subTopics.isEmpty());
		assertTrue(session.subQos.isEmpty());
		assertTrue(session.unsubTopics.isEmpty());
		MqttSubscriber.unsubscribe(session, testTopicFilters);
		session.checkUnsubscribe(testTopicFilters);
	}

	@Test
	public void testIsMessageEmpty() {
		MqttBufferedSessionImpl session;
		
		// check null
		try {
			MqttSubscriber.isMessageEmpty(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// empty
		session = MqttBufferedSessionImplCoreTest.createSessionEmpty();
		assertTrue(MqttSubscriber.isMessageEmpty(session));
		
		// not empty
		session = MqttBufferedSessionImplCoreTest.createSessionFillAll();
		assertFalse(MqttSubscriber.isMessageEmpty(session));
	}

	@Test
	public void testGetMessageCount() {
		MqttBufferedSessionImpl session;
		
		// check null
		try {
			MqttSubscriber.getMessageCount(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// empty
		session = MqttBufferedSessionImplCoreTest.createSessionEmpty();
		assertEquals(MqttSubscriber.getMessageCount(session), 0);
		
		// not empty
		session = MqttBufferedSessionImplCoreTest.createSessionFillAll();
		assertEquals(MqttSubscriber.getMessageCount(session), session.getMessageCount());
	}

	@Test
	public void testClearMessages() {
		MqttBufferedSessionImpl session;
		
		// check null
		try {
			MqttSubscriber.clearMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check
		session = MqttBufferedSessionImplCoreTest.createSessionFillAll();
		assertFalse(session.isMessageEmpty());
		MqttSubscriber.clearMessages(session);
		assertTrue(session.isMessageEmpty());
	}

	@Test
	public void testContainsMessage() {
		final String matchedTopicFilter   = MqttBufferedSessionImplCoreTest.allMatchesTopicFilters[0];
		MqttBufferedSessionImpl session;
		
		// check null
		try {
			MqttSubscriber.containsMessage(null, matchedTopicFilter);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// empty
		session = MqttBufferedSessionImplCoreTest.createSessionEmpty();
		assertFalse(MqttSubscriber.containsMessage(session, matchedTopicFilter));
		
		// not empty
		session = MqttBufferedSessionImplCoreTest.createSessionFillAll();
		assertTrue(MqttSubscriber.containsMessage(session, matchedTopicFilter));
	}

	@Test
	public void testContainsAllMessages() {
		MqttBufferedSessionImpl session;
		
		// check null
		try {
			MqttSubscriber.containsAllMessages(null, Arrays.asList(MqttBufferedSessionImplCoreTest.allMatchesTopicFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// empty
		session = MqttBufferedSessionImplCoreTest.createSessionEmpty();
		assertFalse(MqttSubscriber.containsAllMessages(session, Arrays.asList(MqttBufferedSessionImplCoreTest.allMatchesTopicFilters)));
		
		// not empty
		session = MqttBufferedSessionImplCoreTest.createSessionFillAll();
		assertTrue(MqttSubscriber.containsAllMessages(session, Arrays.asList(MqttBufferedSessionImplCoreTest.allMatchesTopicFilters)));
	}

	@Test
	public void testWaitMessageMqttBufferedSession() throws InterruptedException
	{
		// check null
		try {
			MqttSubscriber.waitMessage(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		assertFalse(session.calledWaitMessageVoid);
		MqttSubscriber.waitMessage(session);
		assertTrue(session.calledWaitMessageVoid);
	}

	@Test
	public void testWaitMessageMqttBufferedSessionString() throws InterruptedException
	{
		final String matchedTopicFilter   = MqttBufferedSessionImplCoreTest.allMatchesTopicFilters[0];
		
		// check null
		try {
			MqttSubscriber.waitMessage(null, matchedTopicFilter);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		assertFalse(session.calledWaitMessageString);
		assertTrue(session.inTopicFilters.isEmpty());
		MqttSubscriber.waitMessage(session, matchedTopicFilter);
		assertTrue(session.calledWaitMessageString);
		assertEquals(session.inTopicFilters.size(), 1);
		assertEquals(session.inTopicFilters.get(0), matchedTopicFilter);
	}

	@Test
	public void testWaitAllMessages() throws InterruptedException
	{
		// check null
		try {
			MqttSubscriber.waitAllMessages(null, Arrays.asList(MqttBufferedSessionImplCoreTest.allMatchesTopicFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		assertFalse(session.calledWaitAllMessages);
		assertTrue(session.inTopicFilters.isEmpty());
		MqttSubscriber.waitAllMessages(session, Arrays.asList(MqttBufferedSessionImplCoreTest.allMatchesTopicFilters));
		assertTrue(session.calledWaitAllMessages);
		assertEquals(session.inTopicFilters.size(), MqttBufferedSessionImplCoreTest.allMatchesTopicFilters.length);
		assertEquals(session.inTopicFilters, Arrays.asList(MqttBufferedSessionImplCoreTest.allMatchesTopicFilters));
	}

	@Test
	public void testPopMessage() throws InterruptedException
	{
		MqttBufferedSessionImpl session;
		
		// check null
		try {
			MqttSubscriber.popMessage(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		session = MqttBufferedSessionImplCoreTest.createSessionFillAll();
		MqttBufferedSessionImplCoreTest.setConnectionFlag(session);
		MqttArrivedMessage ansmsg = session.getMessage(0L, false);
		MqttArrivedMessage retmsg = MqttSubscriber.popMessage(session);
		assertTrue(retmsg == ansmsg);
	}

	@Test
	public void testPopFilteredMessage() throws InterruptedException
	{
		final String matchedTopicFilter   = MqttBufferedSessionImplCoreTest.allMatchesTopicFilters[0];
		MqttBufferedSessionImpl session;
		
		// check null
		try {
			MqttSubscriber.popFilteredMessage(null, matchedTopicFilter);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		session = MqttBufferedSessionImplCoreTest.createSessionFillAll();
		MqttBufferedSessionImplCoreTest.setConnectionFlag(session);
		MqttArrivedMessage ansmsg = session.getFilteredMessage(matchedTopicFilter, 0L, false);
		MqttArrivedMessage retmsg = MqttSubscriber.popFilteredMessage(session, matchedTopicFilter);
		assertTrue(retmsg == ansmsg);
	}

	@Test
	public void testPopStringString() throws InterruptedException
	{
		// check null
		try {
			MqttSubscriber.popString(null, "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData2, "UTF-8"), 1, true);
		assertFalse(session.isMessageEmpty());
		
		String ret = MqttSubscriber.popString(session, "UTF-8");
		assertTrue(session.isMessageEmpty());
		assertEquals(ret, TestStringData2);
	}

	@Test
	public void testPopString() throws InterruptedException
	{
		// check null
		try {
			MqttSubscriber.popString(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData2, null), 1, true);
		assertFalse(session.isMessageEmpty());
		
		String ret = MqttSubscriber.popString(session);
		assertTrue(session.isMessageEmpty());
		assertEquals(ret, TestStringData2);
	}

	@Test
	public void testPopStringSJIS() throws InterruptedException
	{
		// check null
		try {
			MqttSubscriber.popStringSJIS(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData2, "MS932"), 1, true);
		assertFalse(session.isMessageEmpty());
		
		String ret = MqttSubscriber.popStringSJIS(session);
		assertTrue(session.isMessageEmpty());
		assertEquals(ret, TestStringData2);
	}

	@Test
	public void testPopStringUTF8() throws InterruptedException
	{
		// check null
		try {
			MqttSubscriber.popStringUTF8(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData2, "UTF-8"), 1, true);
		assertFalse(session.isMessageEmpty());
		
		String ret = MqttSubscriber.popStringUTF8(session);
		assertTrue(session.isMessageEmpty());
		assertEquals(ret, TestStringData2);
	}

	@Test
	public void testPopFilteredStringStringString() throws InterruptedException
	{
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		
		// check null
		try {
			MqttSubscriber.popFilteredString(null, testTopic2, "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttSubscriber.popFilteredString(session, null, "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		// check empty topic
		try {
			MqttSubscriber.popFilteredString(session, "", "UTF-8");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		// check call
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData1, "UTF-8"), 1, true);
		session.putMessage(testTopic2, getStringBytes(TestStringData2, "UTF-8"), 1, true);
		assertEquals(session.getMessageCount(), 2);
		
		String ret = MqttSubscriber.popFilteredString(session, testTopic2, "UTF-8");
		assertEquals(ret, TestStringData2);
		ret = MqttSubscriber.popFilteredString(session, testTopic1, "UTF-8");
		assertEquals(ret, TestStringData1);
		assertTrue(session.isMessageEmpty());
	}

	@Test
	public void testPopFilteredStringString() throws InterruptedException
	{
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		
		// check null
		try {
			MqttSubscriber.popFilteredString(null, testTopic2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttSubscriber.popFilteredString(session, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		// check empty topic
		try {
			MqttSubscriber.popFilteredString(session, "");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		// check call
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData1, null), 1, true);
		session.putMessage(testTopic2, getStringBytes(TestStringData2, null), 1, true);
		assertEquals(session.getMessageCount(), 2);
		
		String ret = MqttSubscriber.popFilteredString(session, testTopic2);
		assertEquals(ret, TestStringData2);
		ret = MqttSubscriber.popFilteredString(session, testTopic1);
		assertEquals(ret, TestStringData1);
		assertTrue(session.isMessageEmpty());
	}

	@Test
	public void testPopFilteredStringSJIS() throws InterruptedException
	{
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		
		// check null
		try {
			MqttSubscriber.popFilteredStringSJIS(null, testTopic2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttSubscriber.popFilteredStringSJIS(session, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		// check empty topic
		try {
			MqttSubscriber.popFilteredStringSJIS(session, "");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		// check call
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData1, "MS932"), 1, true);
		session.putMessage(testTopic2, getStringBytes(TestStringData2, "MS932"), 1, true);
		assertEquals(session.getMessageCount(), 2);
		
		String ret = MqttSubscriber.popFilteredStringSJIS(session, testTopic2);
		assertEquals(ret, TestStringData2);
		ret = MqttSubscriber.popFilteredStringSJIS(session, testTopic1);
		assertEquals(ret, TestStringData1);
		assertTrue(session.isMessageEmpty());
	}

	@Test
	public void testPopFilteredStringUTF8() throws InterruptedException
	{
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		
		// check null
		try {
			MqttSubscriber.popFilteredStringUTF8(null, testTopic2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttSubscriber.popFilteredStringUTF8(session, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		// check empty topic
		try {
			MqttSubscriber.popFilteredStringUTF8(session, "");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		// check call
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData1, "UTF-8"), 1, true);
		session.putMessage(testTopic2, getStringBytes(TestStringData2, "UTF-8"), 1, true);
		assertEquals(session.getMessageCount(), 2);
		
		String ret = MqttSubscriber.popFilteredStringUTF8(session, testTopic2);
		assertEquals(ret, TestStringData2);
		ret = MqttSubscriber.popFilteredStringUTF8(session, testTopic1);
		assertEquals(ret, TestStringData1);
		assertTrue(session.isMessageEmpty());
	}

	@Test
	public void testPopBinaryFile() throws InterruptedException
	{
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		
		// check null
		try {
			MqttSubscriber.popBinaryFile(null, fnameOutputUTF8);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.putMessage(testTopic1, getStringBytes(TestStringData2, "UTF-8"), 1, true);
			MqttSubscriber.popBinaryFile(session, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData2, "UTF-8"), 1, true);
		assertFalse(session.isMessageEmpty());

		MqttSubscriber.popBinaryFile(session, fnameOutputUTF8);
		assertTrue(session.isMessageEmpty());
		
		byte[] read = loadFile(fnameOutputUTF8);
		assertTrue(Arrays.equals(read, getStringBytes(TestStringData2, "UTF-8")));
	}

	@Test
	public void testPopFilteredBinaryFile() throws InterruptedException
	{
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		
		// check null
		try {
			MqttSubscriber.popFilteredBinaryFile(null, testTopic2, fnameOutputUTF8);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttSubscriber.popFilteredBinaryFile(session, null, fnameOutputUTF8);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.putMessage(testTopic2, getStringBytes(TestStringData2, "UTF-8"), 1, true);
			MqttSubscriber.popFilteredBinaryFile(session, testTopic2, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		// check empty topic
		try {
			MqttSubscriber.popFilteredBinaryFile(session, "", fnameOutputUTF8);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		// check call
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData1, "UTF-8"), 1, true);
		session.putMessage(testTopic2, getStringBytes(TestStringData2, "UTF-8"), 1, true);
		assertEquals(session.getMessageCount(), 2);
		
		// topic 2
		MqttSubscriber.popFilteredBinaryFile(session, testTopic2, fnameOutputUTF8);
		byte[] read = loadFile(fnameOutputUTF8);
		assertTrue(Arrays.equals(read, getStringBytes(TestStringData2, "UTF-8")));
		
		// topic 2
		MqttSubscriber.popFilteredBinaryFile(session, testTopic1, fnameOutputUTF8);
		read = loadFile(fnameOutputUTF8);
		assertTrue(Arrays.equals(read, getStringBytes(TestStringData1, "UTF-8")));
		
		// check session
		assertTrue(session.isMessageEmpty());
	}

	@Test
	public void testPopTextFile() throws InterruptedException
	{
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		
		// check null
		try {
			MqttSubscriber.popTextFile(null, fnameOutputEUC, "UTF-8", "EUC-JP");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.putMessage(testTopic1, getStringBytes(TestStringData2, "UTF-8"), 1, true);
			MqttSubscriber.popTextFile(session, null, "UTF-8", "EUC-JP");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// check call
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData2, "UTF-8"), 1, true);
		assertFalse(session.isMessageEmpty());

		MqttSubscriber.popTextFile(session, fnameOutputEUC, "UTF-8", "EUC-JP");
		assertTrue(session.isMessageEmpty());
		
		byte[] read = loadFile(fnameOutputEUC);
		assertTrue(Arrays.equals(read, getStringBytes(TestStringData2, "EUC-JP")));
	}

	@Test
	public void testPopFilteredTextFile() throws InterruptedException
	{
		CheckSubscriberTestSession session = new CheckSubscriberTestSession();
		
		// check null
		try {
			MqttSubscriber.popFilteredTextFile(null, testTopic2, fnameOutputEUC, "UTF-8", "EUC-JP");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttSubscriber.popFilteredTextFile(session, null, fnameOutputEUC, "UTF-8", "EUC-JP");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.putMessage(testTopic2, getStringBytes(TestStringData2, "UTF-8"), 1, true);
			MqttSubscriber.popFilteredTextFile(session, testTopic2, null, "UTF-8", "EUC-JP");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		// check empty topic
		try {
			MqttSubscriber.popFilteredTextFile(session, "", fnameOutputEUC, "UTF-8", "EUC-JP");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		// check call
		assertTrue(session.isMessageEmpty());
		session.putMessage(testTopic1, getStringBytes(TestStringData1, "UTF-8"), 1, true);
		session.putMessage(testTopic2, getStringBytes(TestStringData2, "UTF-8"), 1, true);
		assertEquals(session.getMessageCount(), 2);
		
		// topic 2
		MqttSubscriber.popFilteredTextFile(session, testTopic2, fnameOutputEUC, "UTF-8", "EUC-JP");
		byte[] read = loadFile(fnameOutputEUC);
		assertTrue(Arrays.equals(read, getStringBytes(TestStringData2, "EUC-JP")));
		
		// topic 2
		MqttSubscriber.popFilteredTextFile(session, testTopic1, fnameOutputEUC, "UTF-8", "EUC-JP");
		read = loadFile(fnameOutputEUC);
		assertTrue(Arrays.equals(read, getStringBytes(TestStringData1, "EUC-JP")));
		
		// check session
		assertTrue(session.isMessageEmpty());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static class SubscribeTestSession extends MqttBufferedSessionImpl
	{
		public ArrayList<String> subTopics = new ArrayList<String>();
		public ArrayList<String> unsubTopics = new ArrayList<String>();
		public ArrayList<Integer> subQos = new ArrayList<Integer>();
		
		public SubscribeTestSession() {
			super(new MqttConnectionParams());
			clear();
		}
		
		public void checkSubscribe(String topicFilter, int qos) {
			Integer objQos = Integer.valueOf(qos);
			assertTrue(unsubTopics.isEmpty());
			assertEquals(subTopics.size(), 1);
			assertEquals(subQos.size(), 1);
			assertEquals(subTopics.get(0), topicFilter);
			assertEquals(subQos.get(0), objQos);
		}
		
		public void checkSubscribe(List<String> topicFilters, int qos) {
			Integer objQos = Integer.valueOf(qos);
			assertTrue(unsubTopics.isEmpty());
			assertEquals(subTopics, topicFilters);
			assertEquals(subTopics.size(), subQos.size());
			for (int i = 0; i < subQos.size(); i++) {
				assertEquals("QoS[" + i + "] : ", subQos.get(i), objQos);
			}
		}
		
		public void checkUnsubscribe(String topicFilter) {
			assertTrue(subTopics.isEmpty());
			assertTrue(subQos.isEmpty());
			assertEquals(unsubTopics.size(), 1);
			assertEquals(unsubTopics.get(0), topicFilter);
		}
		
		public void checkUnsubscribe(List<String> topicFilters) {
			assertTrue(subTopics.isEmpty());
			assertTrue(subQos.isEmpty());
			assertEquals(unsubTopics, topicFilters);
		}
		
		public void clear() {
			subTopics.clear();
			unsubTopics.clear();
			subQos.clear();
		}

		@Override
		public void subscribe(String topic) {
			if (topic == null)
				throw new NullPointerException();
			subTopics.add(topic);
			subQos.add(1);
		}

		@Override
		public void subscribe(String topic, int qos) {
			if (topic == null)
				throw new NullPointerException();
			subTopics.add(topic);
			subQos.add(qos);
		}

		@Override
		public void subscribe(String[] topicFilters) {
			for (int i = 0; i < topicFilters.length; i++) {
				subQos.add(1);
			}
			subTopics.addAll(Arrays.asList(topicFilters));
		}

		@Override
		public void subscribe(String[] topicFilters, int[] qos) {
			subTopics.ensureCapacity(topicFilters.length);
			subQos.ensureCapacity(qos.length);
			
			subTopics.addAll(Arrays.asList(topicFilters));
			for (int q : qos) {
				subQos.add(q);
			}
		}

		@Override
		public void unsubscribe(String topic) {
			if (topic == null)
				throw new NullPointerException();
			unsubTopics.add(topic);
		}

		@Override
		public void unsubscribe(String[] topicFilters) {
			unsubTopics.addAll(Arrays.asList(topicFilters));
		}
	}
	
	static class CheckSubscriberTestSession extends MqttBufferedSessionImpl
	{
		public ArrayList<String> inTopicFilters = new ArrayList<String>();
		public boolean calledWaitMessageVoid;
		public boolean calledWaitMessageString;
		public boolean calledWaitAllMessages;
		
		public CheckSubscriberTestSession() {
			super(new MqttConnectionParams());
			MqttBufferedSessionImplCoreTest.setConnectionFlag(this);
			clear();
		}
		
		public void clear() {
			calledWaitMessageVoid = false;
			calledWaitMessageString = false;
			calledWaitAllMessages = false;
			inTopicFilters.clear();
		}

		@Override
		public void waitMessage() throws InterruptedException {
			calledWaitMessageVoid = true;
		}

		@Override
		public void waitMessage(String topicFilter) throws InterruptedException {
			calledWaitMessageString = true;
			inTopicFilters.add(topicFilter);
		}

		@Override
		public void waitAllMessages(List<String> topicFilters) throws InterruptedException
		{
			calledWaitAllMessages = true;
			inTopicFilters.addAll(topicFilters);
		}
		
		public void putMessage(String topic, byte[] message, int qos, boolean retained) {
			MqttArrivedMessage mmsg = new MqttArrivedMessage(topic, message, qos, retained, false);
			try {
				onMessageArrived(mmsg);
			} catch (Exception ex) {
				onConnectionLost(ex);
			}
		}
	}
}
