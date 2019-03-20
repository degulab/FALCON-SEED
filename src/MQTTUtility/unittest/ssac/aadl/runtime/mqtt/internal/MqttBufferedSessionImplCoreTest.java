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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;

import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.MqttRuntimeException;

public class MqttBufferedSessionImplCoreTest
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static class TopicPatternPair {
		public final String		topicString;
		public final Pattern	topicPattern;
		
		public TopicPatternPair(String string, Pattern pattern) {
			topicString = string;
			topicPattern = pattern;
		}
	}
	
	static class MessageMatchedData {
		public final String				topicString;
		public final Pattern			topicPattern;
		public final MqttArrivedMessage	message;
		
		public MessageMatchedData(String string, MqttArrivedMessage amsg) {
			topicString  = string;
			if (MqttBufferedSessionImpl.isFilterPatternTopic(string)) {
				topicPattern = MqttBufferedSessionImpl.createTopicPattern(string);
			} else {
				topicPattern = MqttBufferedSessionImpl._emptyPattern;
			}
			//topicPattern = MqttBufferedSessionImpl._emptyPattern;
			message      = amsg;
		}
	}
	
	static String makeString(char...cs) {
		return new String(cs);
	}
	
	static String toIndexMessage(int index) {
		return ("index[" + index + "]");
	}
	
	static TopicPatternPair[] topicPatterns = {
		// no wildcard
		new TopicPatternPair(makeString('h','o','g','e'), MqttBufferedSessionImpl._emptyPattern),
		new TopicPatternPair(makeString('/','m','q','t','t','/','t','e','s','t','/','t','o','p','i','c','1'), MqttBufferedSessionImpl._emptyPattern),
		new TopicPatternPair(makeString('/','m','q','t','t','/','t','e','s','t','/','t','o','p','i','c','2'), MqttBufferedSessionImpl._emptyPattern),
		new TopicPatternPair(makeString('/','m','q','t','t','/','t','e','s','t','/','t','o','p','i','c','3'), MqttBufferedSessionImpl._emptyPattern),
		// wildcard included
		new TopicPatternPair(makeString('#'), Pattern.compile("[^/]*(?:/[^/]*)*", Pattern.DOTALL)),
		new TopicPatternPair(makeString('#','#','h','o','g','e'), Pattern.compile("[^/]*(?:/[^/]*)*", Pattern.DOTALL)),
		new TopicPatternPair(makeString('h','o','g','e','#'), Pattern.compile("\\Qhoge\\E(?:/[^/]*)*", Pattern.DOTALL)),
		new TopicPatternPair(makeString('h','o','g','e','/','#'), Pattern.compile("\\Qhoge\\E(?:/[^/]*)*", Pattern.DOTALL)),
		new TopicPatternPair(makeString('m','q','t','t','/','t','e','s','t','/','t','o','p','i','c','#'), Pattern.compile("\\Qmqtt/test/topic\\E(?:/[^/]*)*", Pattern.DOTALL)),
		new TopicPatternPair(makeString('+'), Pattern.compile("\\Q\\E[^/]+\\Q\\E", Pattern.DOTALL)),
		new TopicPatternPair(makeString('h','o','g','e','/','+'), Pattern.compile("\\Qhoge/\\E[^/]+\\Q\\E", Pattern.DOTALL)),
		new TopicPatternPair(makeString('/','m','q','t','t','/','t','e','s','t','/','+','/','t','o','p','i','c','#'), Pattern.compile("\\Q/mqtt/test/\\E[^/]+\\Q/topic\\E(?:/[^/]*)*", Pattern.DOTALL)),
	};
	
	static MqttArrivedMessage[] testMessageData = {
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/1", "001".getBytes(), 0, false, false),
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/2", "002".getBytes(), 1, true, false),
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/3", "003".getBytes(), 2, false, true),
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/4", "004".getBytes(), 0, true, true),
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/5", "005".getBytes(), 1, false, false),
		new MqttArrivedMessage("/mqtt/unittest/hoge1/data/1", "006".getBytes(), 2, false, false),
		new MqttArrivedMessage("/mqtt/unittest/hoge2/data/2", "007".getBytes(), 0, true, false),
		new MqttArrivedMessage("/mqtt/unittest/hoge3/data/3", "008".getBytes(), 1, false, true),
		new MqttArrivedMessage("/mqtt/unittest/hoge4/data/4", "009".getBytes(), 2, true, true),
		new MqttArrivedMessage("/mqtt/unittest/hoge5/data/5", "010".getBytes(), 0, false, false),
		new MqttArrivedMessage("/mqtt/unittest/sub/1", "011".getBytes(), 1, false, false),
		new MqttArrivedMessage("/mqtt/unittest/sub/2", "012".getBytes(), 2, true, false),
		new MqttArrivedMessage("/mqtt/unittest/sub/3", "013".getBytes(), 0, false, true),
		new MqttArrivedMessage("/mqtt/unittest/sub/4", "014".getBytes(), 1, true, true),
		new MqttArrivedMessage("/mqtt/unittest/sub/5", "015".getBytes(), 2, false, false),
	};
	
	static MessageMatchedData[] testMatchedMessageData00 = {
		new MessageMatchedData("/mqtt/unittest/arrived/test/1", testMessageData[0]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/2", testMessageData[1]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/3", testMessageData[2]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/4", testMessageData[3]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/5", testMessageData[4]),
		new MessageMatchedData("/mqtt/unittest/hoge1/data/1", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/hoge2/data/2", testMessageData[6]),
		new MessageMatchedData("/mqtt/unittest/hoge3/data/3", testMessageData[7]),
		new MessageMatchedData("/mqtt/unittest/hoge4/data/4", testMessageData[8]),
		new MessageMatchedData("/mqtt/unittest/hoge5/data/5", testMessageData[9]),
		new MessageMatchedData("/mqtt/unittest/sub/1", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/2", testMessageData[11]),
		new MessageMatchedData("/mqtt/unittest/sub/3", testMessageData[12]),
		new MessageMatchedData("/mqtt/unittest/sub/4", testMessageData[13]),
		new MessageMatchedData("/mqtt/unittest/sub/5", testMessageData[14]),
	};
	
	static MessageMatchedData[] testMatchedMessageData01 = {
		new MessageMatchedData("/mqtt/unittest/arrived/test/1", testMessageData[0]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/2", testMessageData[1]),
		new MessageMatchedData("/mqtt/unittest/derived/test/2", null),
		new MessageMatchedData("/mqtt/unittest/arrived/test/3", testMessageData[2]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/4", testMessageData[3]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/5", testMessageData[4]),
		new MessageMatchedData("/mqtt/unittest/sub/1", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/2", testMessageData[11]),
		new MessageMatchedData("/mqtt/unittest/sub/3", testMessageData[12]),
		new MessageMatchedData("/mqtt/unittest/pub/3", null),
		new MessageMatchedData("/mqtt/unittest/sub/4", testMessageData[13]),
		new MessageMatchedData("/mqtt/unittest/sub/5", testMessageData[14]),
		new MessageMatchedData("/mqtt/unittest/hoge1/data/1", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/hoge2/data/2", testMessageData[6]),
		new MessageMatchedData("/mqtt/unittest/hoge3/data/3", testMessageData[7]),
		new MessageMatchedData("/mqtt/unittest/hoge4/data/4", testMessageData[8]),
		new MessageMatchedData("/mqtt/test/hoge4/data/4", null),
		new MessageMatchedData("/mqtt/unittest/hoge5/data/5", testMessageData[9]),
	};
	
	static MessageMatchedData[] testMatchedMessageData10 = {
		new MessageMatchedData("/mqtt/unittest/+/test/1", testMessageData[0]),
		new MessageMatchedData("/mqtt/unittest/+/test/2", testMessageData[1]),
		new MessageMatchedData("/mqtt/unittest/+/test/3", testMessageData[2]),
		new MessageMatchedData("/mqtt/unittest/+/test/4", testMessageData[3]),
		new MessageMatchedData("/mqtt/unittest/+/test/5", testMessageData[4]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[6]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[7]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[8]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[9]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[11]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[12]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[13]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[14]),
	};
	
	static MessageMatchedData[] testMatchedMessageData11_remove = {
		new MessageMatchedData("/mqtt/unittest/+/test/1", testMessageData[0]),
		new MessageMatchedData("/mqtt/unittest/+/test/2", testMessageData[1]),
		new MessageMatchedData("/mqtt/unittest/+/arrived/test/2", null),
		new MessageMatchedData("/mqtt/unittest/+/test/3", testMessageData[2]),
		new MessageMatchedData("/mqtt/unittest/+/test/4", testMessageData[3]),
		new MessageMatchedData("/mqtt/unittest/+/test/5", testMessageData[4]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[11]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[12]),
		new MessageMatchedData("/mqtt/unittest/pub/#", null),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[13]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[14]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[6]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[7]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[8]),
		new MessageMatchedData("/mqtt/test/+/data/#", null),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[9]),
	};
	
	static MessageMatchedData[] testMatchedMessageData11_noremove = {
		new MessageMatchedData("/mqtt/unittest/+/test/1", testMessageData[0]),
		new MessageMatchedData("/mqtt/unittest/+/test/2", testMessageData[1]),
		new MessageMatchedData("/mqtt/unittest/+/arrived/test/2", null),
		new MessageMatchedData("/mqtt/unittest/+/test/3", testMessageData[2]),
		new MessageMatchedData("/mqtt/unittest/+/test/4", testMessageData[3]),
		new MessageMatchedData("/mqtt/unittest/+/test/5", testMessageData[4]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/pub/#", null),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
		new MessageMatchedData("/mqtt/test/+/data/#", null),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
	};
	
	static MessageMatchedData[] testMatchedMessageData_mixed_ordered = {
		new MessageMatchedData("/mqtt/unittest/arrived/test/1", testMessageData[0]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/2", testMessageData[1]),
		new MessageMatchedData("/mqtt/unittest/arrived/test/3", testMessageData[2]),
		new MessageMatchedData("/mqtt/unittest/+/test/4", testMessageData[3]),
		new MessageMatchedData("/mqtt/unittest/+/test/5", testMessageData[4]),
		new MessageMatchedData("/mqtt/unittest/hoge1/data/1", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[6]),
		new MessageMatchedData("/mqtt/unittest/hoge3/data/3", testMessageData[7]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[8]),
		new MessageMatchedData("/mqtt/unittest/hoge5/data/5", testMessageData[9]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/2", testMessageData[11]),
		new MessageMatchedData("/mqtt/unittest/sub/3", testMessageData[12]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[13]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[14]),
	};
	
	static MessageMatchedData[] testMatchedMessageData_mixed_remove = {
		new MessageMatchedData("/mqtt/unittest/arrived/test/1", testMessageData[0]),
		new MessageMatchedData("/mqtt/unittest/derived/test/2", null),
		new MessageMatchedData("/mqtt/unittest/arrived/test/2", testMessageData[1]),
		new MessageMatchedData("/mqtt/unittest/+/arrived/test/2", null),
		new MessageMatchedData("/mqtt/unittest/arrived/test/3", testMessageData[2]),
		new MessageMatchedData("/mqtt/unittest/+/test/4", testMessageData[3]),
		new MessageMatchedData("/mqtt/unittest/+/test/5", testMessageData[4]),
		new MessageMatchedData("/mqtt/unittest/hoge1/data/1", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[6]),
		new MessageMatchedData("/mqtt/test/hoge4/data/3", null),
		new MessageMatchedData("/mqtt/unittest/hoge3/data/3", testMessageData[7]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[8]),
		new MessageMatchedData("/mqtt/test/+/data/#", null),
		new MessageMatchedData("/mqtt/unittest/hoge5/data/5", testMessageData[9]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/2", testMessageData[11]),
		new MessageMatchedData("/mqtt/unittest/pub/3", null),
		new MessageMatchedData("/mqtt/unittest/pub/#", null),
		new MessageMatchedData("/mqtt/unittest/sub/3", testMessageData[12]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[13]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[14]),
	};
	
	static MessageMatchedData[] testMatchedMessageData_mixed_noremove = {
		new MessageMatchedData("/mqtt/unittest/arrived/test/1", testMessageData[0]),
		new MessageMatchedData("/mqtt/unittest/derived/test/2", null),
		new MessageMatchedData("/mqtt/unittest/arrived/test/2", testMessageData[1]),
		new MessageMatchedData("/mqtt/unittest/+/arrived/test/2", null),
		new MessageMatchedData("/mqtt/unittest/arrived/test/3", testMessageData[2]),
		new MessageMatchedData("/mqtt/unittest/+/test/4", testMessageData[3]),
		new MessageMatchedData("/mqtt/unittest/+/test/5", testMessageData[4]),
		new MessageMatchedData("/mqtt/unittest/hoge1/data/1", testMessageData[5]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
		new MessageMatchedData("/mqtt/test/hoge4/data/3", null),
		new MessageMatchedData("/mqtt/unittest/hoge3/data/3", testMessageData[7]),
		new MessageMatchedData("/mqtt/unittest/+/data/#", testMessageData[5]),
		new MessageMatchedData("/mqtt/test/+/data/#", null),
		new MessageMatchedData("/mqtt/unittest/hoge5/data/5", testMessageData[9]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/2", testMessageData[11]),
		new MessageMatchedData("/mqtt/unittest/pub/3", null),
		new MessageMatchedData("/mqtt/unittest/pub/#", null),
		new MessageMatchedData("/mqtt/unittest/sub/3", testMessageData[12]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
		new MessageMatchedData("/mqtt/unittest/sub/#", testMessageData[10]),
	};
	
	static public String[] noMatchesTopicFilters = {
		"/mqtt/unittest/derived/test/2",
		"/mqtt/unittest/+/arrived/test/2",
		"/mqtt/test/hoge4/data/3",
		"/mqtt/test/+/data/#",
		"/mqtt/unittest/pub/3",
		"/mqtt/unittest/pub/#",
	};
	static public String[] allMatchesTopicFilters = {
		"/mqtt/unittest/arrived/test/1",
		"/mqtt/unittest/+/test/4",
		"/mqtt/unittest/hoge1/data/1",
		"/mqtt/unittest/+/data/#",
		"/mqtt/unittest/sub/3",
		"/mqtt/unittest/sub/#",
	};
	static public String[] mixedMatchesTopicFilters = {
		"/mqtt/unittest/derived/test/2",	// no matches
		"/mqtt/unittest/arrived/test/1",
		"/mqtt/unittest/+/test/4",
		"/mqtt/unittest/+/arrived/test/2",	// no matches
		"/mqtt/test/hoge4/data/3",	// no matches
		"/mqtt/unittest/hoge1/data/1",
		"/mqtt/unittest/+/data/#",
		"/mqtt/test/+/data/#",	// no matches
		"/mqtt/unittest/pub/3",	// no matches
		"/mqtt/unittest/sub/3",
		"/mqtt/unittest/pub/#",	// no matches
		"/mqtt/unittest/sub/#",
	};
	
	static MqttArrivedMessage[] testAllMatchesMessageData = {
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/1", "001".getBytes(), 0, false, false),
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/2", "002".getBytes(), 1, true, false),
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/3", "003".getBytes(), 2, false, true),
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/4", "004".getBytes(), 0, true, true),
		new MqttArrivedMessage("/mqtt/unittest/arrived/test/5", "005".getBytes(), 1, false, false),
		new MqttArrivedMessage("/mqtt/unittest/hoge1/data/1", "006".getBytes(), 2, false, false),
		new MqttArrivedMessage("/mqtt/unittest/hoge2/data/2", "007".getBytes(), 0, true, false),
		new MqttArrivedMessage("/mqtt/unittest/hoge3/data/3", "008".getBytes(), 1, false, true),
		new MqttArrivedMessage("/mqtt/unittest/hoge4/data/4", "009".getBytes(), 2, true, true),
		new MqttArrivedMessage("/mqtt/unittest/hoge5/data/5", "010".getBytes(), 0, false, false),
		new MqttArrivedMessage("/mqtt/unittest/sub/1", "011".getBytes(), 1, false, false),
		new MqttArrivedMessage("/mqtt/unittest/sub/2", "012".getBytes(), 2, true, false),
		new MqttArrivedMessage("/mqtt/unittest/sub/3", "013".getBytes(), 0, false, true),
	};
	
	static public String[] includeNullFilters = {
		"/mqtt/unittest/arrived/test/1",
		null,
		"/mqtt/unittest/+/arrived/test/2",	// no matches
	};
	static public String[] includeEmptyFilters = {
		"/mqtt/unittest/arrived/test/1",
		"",
		"/mqtt/unittest/+/arrived/test/2",	// no matches
	};
	
	static Map<String, Pattern> createPatternMap(String...topics) {
		HashMap<String, Pattern> map = new HashMap<String, Pattern>();
		for (String topic : topics) {
			if (MqttBufferedSessionImpl.isFilterPatternTopic(topic)) {
				// pattern
				map.put(topic, MqttBufferedSessionImpl.createTopicPattern(topic));
			} else {
				// fixed
				map.put(topic, MqttBufferedSessionImpl._emptyPattern);
			}
		}
		return map;
	}
	
	static public void setConnectionFlag(MqttBufferedSessionImpl session) {
		session._disconnected = false;
	}
	
	static public MqttBufferedSessionImpl createSessionEmpty() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		return session;
	}
	
	static public MqttBufferedSessionImpl createSessionFillCache() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// set data
		for (MqttArrivedMessage msg : testMessageData) {
			session._listCache.add(msg);
		}
		assertFalse(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertEquals(session._listCache.size(), testMessageData.length);

		return session;
	}
	
	static public MqttBufferedSessionImpl createSessionFillBuffer() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// set data
		for (MqttArrivedMessage msg : testMessageData) {
			session._listBuffer.add(msg);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);

		return session;
	}
	
	static public MqttBufferedSessionImpl createSessionFillAll() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// set data
		final int bufLen = 8;
		int len = 0;
		for (MqttArrivedMessage msg : testMessageData) {
			if (len < bufLen)
				session._listBuffer.add(msg);
			else
				session._listCache.add(msg);
			len++;
		}
		assertFalse(session._listBuffer.isEmpty());
		assertFalse(session._listCache.isEmpty());
		assertEquals(session._listBuffer.size(), bufLen);
		assertEquals(session._listCache.size(), testMessageData.length - bufLen);

		return session;
	}

	/**
	 * 指定されたメッセージデータの中で、指定されたトピック配列に含まれるパターンに
	 * マッチする要素数をカウントする。
	 */
	static int countMatches(MqttArrivedMessage[] msgary, String[] topicary) {
		int matches = 0;
		for (MqttArrivedMessage msg : msgary) {
			for (String topic : topicary) {
				if (MqttBufferedSessionImpl.isFilterPatternTopic(topic)) {
					// pattern
					Pattern pat = MqttBufferedSessionImpl.createTopicPattern(topic);
					if (pat.matcher(msg.getTopic()).matches()) {
						matches++;
						break;
					}
				} else {
					// fixed
					if (topic.equals(msg.getTopic())) {
						matches++;
						break;
					}
				}
			}
		}
		return matches;
	}
	
	static interface SessionFactory {
		public MqttBufferedSessionImpl createSession();
	}

	static final SessionFactory sessionEmptyFactory = new SessionFactory() {
		@Override
		public MqttBufferedSessionImpl createSession() {
			return createSessionEmpty();
		}
	};
	
	static final SessionFactory sessionFillCacheFactory = new SessionFactory() {
		
		@Override
		public MqttBufferedSessionImpl createSession() {
			return createSessionFillCache();
		}
	};
	
	static final SessionFactory sessionFillBufferFactory = new SessionFactory() {
		
		@Override
		public MqttBufferedSessionImpl createSession() {
			return createSessionFillBuffer();
		}
	};
	
	static final SessionFactory sessionFillAllFactory = new SessionFactory() {
		
		@Override
		public MqttBufferedSessionImpl createSession() {
			return createSessionFillAll();
		}
	};

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	@Test
	public void testIsFilterPatternTopic() {
		// null
		try {
			MqttBufferedSessionImpl.isFilterPatternTopic(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// empty
		assertFalse(MqttBufferedSessionImpl.isFilterPatternTopic(""));
		
		// check
		for (TopicPatternPair pair : topicPatterns) {
			if (pair.topicPattern == MqttBufferedSessionImpl._emptyPattern) {
				assertFalse(pair.topicString, MqttBufferedSessionImpl.isFilterPatternTopic(pair.topicString));
			} else {
				assertTrue(pair.topicString, MqttBufferedSessionImpl.isFilterPatternTopic(pair.topicString));
			}
		}
	}

	@Test
	public void testCreateTopicPattern() {
		// null
		try {
			MqttBufferedSessionImpl.createTopicPattern(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// empty
		Pattern pat = MqttBufferedSessionImpl.createTopicPattern("");
		assertEquals(pat.pattern(), "\\Q\\E");
		
		// check
		for (TopicPatternPair pair : topicPatterns) {
			pat = MqttBufferedSessionImpl.createTopicPattern(pair.topicString);
			if (pair.topicPattern == MqttBufferedSessionImpl._emptyPattern) {
				assertEquals(pair.topicString, pat.pattern(), Pattern.quote(pair.topicString));
			} else {
				assertEquals(pair.topicString, pat.pattern(), pair.topicPattern.pattern());
			}
		}
		
		// check only '#' pattern
		pat = MqttBufferedSessionImpl.createTopicPattern(makeString('#'));
		assertTrue(pat.matcher("").matches());
		assertTrue(pat.matcher(makeString('h','o','g','e')).matches());
		assertTrue(pat.matcher(makeString('h','o','g','e','/')).matches());
		assertTrue(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','/','t','o','p','i','c','1')).matches());
		assertTrue(pat.matcher(makeString('m','q','t','t','/','/','t','e','s','t')).matches());
		
		// check pattern
		pat = MqttBufferedSessionImpl.createTopicPattern(makeString('/','m','q','t','t','/','t','e','s','t','/','+','/','t','o','p','i','c','#'));
		assertTrue(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','/','p','a','t','t','e','r','n','/','t','o','p','i','c')).matches());
		assertTrue(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','/','p','/','t','o','p','i','c','/')).matches());
		assertTrue(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','/','p','/','t','o','p','i','c','/','1')).matches());
		assertTrue(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','/','p','a','t','/','t','o','p','i','c','/','d','a','t','a','/')).matches());
		assertTrue(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','/','p','a','t','/','t','o','p','i','c','/','d','a','t','a','/','v','1','/','v','2')).matches());
		assertFalse(pat.matcher("").matches());
		assertFalse(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t')).matches());
		assertFalse(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','/','/','t','o','p','i','c')).matches());
		assertFalse(pat.matcher(makeString('m','q','t','t','/','t','e','s','t','/','p','a','t','t','e','r','n','/','t','o','p','i','c')).matches());
		assertFalse(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','/','p','a','t','t','e','r','n','/','t','o','p','i','c','1')).matches());
		assertFalse(pat.matcher(makeString('/','m','q','t','t','1','/','t','e','s','t','/','p','a','t','t','e','r','n','/','t','o','p','i','c','/','d','a','t','a','/','v','1')).matches());
		assertFalse(pat.matcher(makeString('/','m','q','t','t','/','t','e','s','t','1','/','p','a','t','t','e','r','n','/','t','o','p','i','c','/','d','a','t','a','/','v','1')).matches());
	}

	@Test
	public void testCachedTopicPattern() {
		// make test data
		String[] testTopics = new String[]{
				makeString('m','q','t','t','U','n','i','t','t','e','s','t','C','a','c','h','e','d','T','o','p','i','c','P','a','t','t','e','r','n'),
				makeString('m','q','t','t','/','u','n','i','t','t','e','s','t','/','c','a','c','h','e','d','/','t','o','p','i','c','/','p','a','t','t','e','r','n','1'),
				makeString('m','q','t','t','/','u','n','i','t','t','e','s','t','/','c','a','c','h','e','d','/','+','/','p','a','t','t','e','r','n','2'),
				makeString('#'),
				makeString('+','/','+','/','+'),
				makeString('m','q','t','t','/','+','/','c','a','c','h','e','d','/','#'),
		};
		int numHasWildcard = 0;
		HashMap<String, Pattern> testmap = new HashMap<String, Pattern>();
		for (String topic : testTopics) {
			if (MqttBufferedSessionImpl.isFilterPatternTopic(topic)) {
				numHasWildcard++;
				testmap.put(topic, MqttBufferedSessionImpl.createTopicPattern(topic));
			} else {
				testmap.put(topic, MqttBufferedSessionImpl._emptyPattern);
			}
		}
		
		// create instance
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._cacheTopicPattern.isEmpty());
		
		// make cache
		for (Map.Entry<String, Pattern> entry : testmap.entrySet()) {
			Pattern resPattern = session.cachedTopicPattern(entry.getKey());
			if (entry.getValue() == MqttBufferedSessionImpl._emptyPattern) {
				// no wildcards
				assertSame(entry.getKey(), resPattern, MqttBufferedSessionImpl._emptyPattern);
			} else {
				// within wildcards
				assertEquals(entry.getKey(), resPattern.pattern(), entry.getValue().pattern());
				entry.setValue(resPattern);	// update pattern instance by cached value
			}
		}
		
		// check cache
		assertEquals(numHasWildcard, session._cacheTopicPattern.size());
		for (Map.Entry<String, Pattern> entry : session._cacheTopicPattern.entrySet()) {
			Map.Entry<String, Pattern> testEntry = null;
			for (Map.Entry<String, Pattern> tentry : testmap.entrySet()) {
				if (entry.getKey().equals(tentry.getKey())) {
					testEntry = tentry;
					break;
				}
			}
			// check
			assertSame(entry.getKey(), entry.getKey(), testEntry.getKey().intern());
			assertSame(entry.getKey(), entry.getValue(), testEntry.getValue());
		}
		
		// check exist cache
		for (Map.Entry<String, Pattern> entry : testmap.entrySet()) {
			Pattern resPattern = session.cachedTopicPattern(entry.getKey());
			assertSame(entry.getKey(), resPattern, entry.getValue());
		}
		
		// clear intern string
		testmap.clear();
		testTopics = null;
		System.gc();
		System.gc();
		//--- check cache (no weak, String#intern() )
		//assertTrue(session._cacheTopicPattern.isEmpty());
	}

	@Test
	public void testGetMessageFromList() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// empty
		assertNull(session.getMessageFromList(false));
		assertNull(session.getMessageFromList(true));
		
		// set data
		for (MqttArrivedMessage msg : testMessageData) {
			session._listBuffer.add(msg);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get without remove
		MqttArrivedMessage firstmsg = testMessageData[0];
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage msg = session.getMessageFromList(false);
			assertSame(toIndexMessage(i), msg, firstmsg);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get with remove
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage msg = session.getMessageFromList(true);
			assertSame(toIndexMessage(i), msg, testMessageData[i]);
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testGetMatchedMessageFromList() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// empty
		MqttArrivedMessage firstmsg = testMessageData[0];
		assertNull(session.getMatchedMessageFromList(0, firstmsg.getTopic(), MqttBufferedSessionImpl._emptyPattern, false));
		assertNull(session.getMatchedMessageFromList(0, firstmsg.getTopic(), MqttBufferedSessionImpl._emptyPattern, true));
		
		//
		// check for fixed topic
		//
		
		// set data
		for (MqttArrivedMessage msg : testMessageData) {
			session._listBuffer.add(msg);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get without remove (ordered)
		for (int i = 0; i < testMatchedMessageData01.length; i++) {
			MessageMatchedData ans = testMatchedMessageData01[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(0, ans.topicString, ans.topicPattern, false);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get without remove (indexed)
		for (int i = 0; i < testMatchedMessageData00.length; i++) {
			MessageMatchedData ans = testMatchedMessageData00[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(i, ans.topicString, ans.topicPattern, false);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get with remove
		for (int i = 0; i < testMatchedMessageData01.length; i++) {
			MessageMatchedData ans = testMatchedMessageData01[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(0, ans.topicString, ans.topicPattern, true);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		//
		// check for wildcard topic
		//
		
		// set data
		for (MqttArrivedMessage msg : testMessageData) {
			session._listBuffer.add(msg);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get without remove (ordered)
		for (int i = 0; i < testMatchedMessageData11_noremove.length; i++) {
			MessageMatchedData ans = testMatchedMessageData11_noremove[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(0, ans.topicString, ans.topicPattern, false);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get without remove (indexed)
		for (int i = 0; i < testMatchedMessageData10.length; i++) {
			MessageMatchedData ans = testMatchedMessageData10[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(i, ans.topicString, ans.topicPattern, false);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get with remove
		for (int i = 0; i < testMatchedMessageData11_remove.length; i++) {
			MessageMatchedData ans = testMatchedMessageData11_remove[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(0, ans.topicString, ans.topicPattern, true);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		//
		// check for mixed topic
		//
		
		// set data
		for (MqttArrivedMessage msg : testMessageData) {
			session._listBuffer.add(msg);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get without remove (ordered)
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData ans = testMatchedMessageData_mixed_noremove[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(0, ans.topicString, ans.topicPattern, false);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get without remove (indexed)
		for (int i = 0; i < testMatchedMessageData_mixed_ordered.length; i++) {
			MessageMatchedData ans = testMatchedMessageData_mixed_ordered[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(i, ans.topicString, ans.topicPattern, false);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// get with remove
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData ans = testMatchedMessageData_mixed_remove[i];
			MqttArrivedMessage msg = session.getMatchedMessageFromList(0, ans.topicString, ans.topicPattern, true);
			assertSame(toIndexMessage(i), msg, ans.message);
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testIsMessageEmpty_GetMessageCount() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// cache empty - buffer empty
		assertTrue(session.isMessageEmpty());
		assertEquals(0, session.getMessageCount());
		
		// cache no empty - buffer empty
		session._listCache.clear();
		session._listBuffer.clear();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		session._listCache.add(testMessageData[0]);
		assertFalse(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.isMessageEmpty());
		assertEquals(1, session.getMessageCount());
		
		// cache empty - buffer no empty
		session._listCache.clear();
		session._listBuffer.clear();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		session._listBuffer.add(testMessageData[0]);
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertFalse(session.isMessageEmpty());
		assertEquals(1, session.getMessageCount());
		
		// cache no empty - buffer no empty
		session._listCache.clear();
		session._listBuffer.clear();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		session._listCache.add(testMessageData[0]);
		session._listBuffer.add(testMessageData[1]);
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertFalse(session.isMessageEmpty());
		assertEquals(2, session.getMessageCount());
	}

	@Test
	public void testClearMessages() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// cache empty - buffer empty
		assertTrue(session.isMessageEmpty());
		session.clearMessages();
		assertTrue(session.isMessageEmpty());
		
		// cache no empty - buffer empty
		session._listCache.clear();
		session._listBuffer.clear();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		session._listCache.add(testMessageData[0]);
		assertFalse(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.isMessageEmpty());
		session.clearMessages();
		assertTrue(session.isMessageEmpty());
		
		// cache empty - buffer no empty
		session._listCache.clear();
		session._listBuffer.clear();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		session._listBuffer.add(testMessageData[0]);
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertFalse(session.isMessageEmpty());
		session.clearMessages();
		assertTrue(session.isMessageEmpty());
		
		// cache no empty - buffer no empty
		session._listCache.clear();
		session._listBuffer.clear();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		session._listCache.add(testMessageData[0]);
		session._listBuffer.add(testMessageData[1]);
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertFalse(session.isMessageEmpty());
		session.clearMessages();
		assertTrue(session.isMessageEmpty());
	}

	@Test
	public void testContainsMessage() {
		final String noMatch1 = "hoge/hoge";
		final String noMatch2 = "hoge/#";
		final String matched1 = "/mqtt/unittest/arrived/test/3";
		final String matched2 = "/mqtt/unittest/sub/#";
		MqttBufferedSessionImpl session;
		
		// empty
		session = createSessionEmpty();
		assertFalse(session.containsMessage(noMatch1));
		assertFalse(session.containsMessage(noMatch2));
		assertFalse(session.containsMessage(matched1));
		assertFalse(session.containsMessage(matched2));
		
		// cache only
		session = createSessionFillCache();
		assertFalse(session.containsMessage(noMatch1));
		assertFalse(session.containsMessage(noMatch2));
		assertTrue(session.containsMessage(matched1));
		assertTrue(session.containsMessage(matched2));
		
		// buffer only
		session = createSessionFillBuffer();
		assertFalse(session.containsMessage(noMatch1));
		assertFalse(session.containsMessage(noMatch2));
		assertTrue(session.containsMessage(matched1));
		assertTrue(session.containsMessage(matched2));
		
		// both
		session = createSessionFillAll();
		assertFalse(session.containsMessage(noMatch1));
		assertFalse(session.containsMessage(noMatch2));
		assertTrue(session.containsMessage(matched1));
		assertTrue(session.containsMessage(matched2));
	}

	@Test
	public void testContainsAllMessages() {
		MqttBufferedSessionImpl session;
		
		// empty
		session = createSessionEmpty();
		//--- check exception
		try {
			session.retainAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		//--- check data
		assertFalse(session.containsAllMessages(Arrays.asList(noMatchesTopicFilters)));
		assertFalse(session.containsAllMessages(Arrays.asList(mixedMatchesTopicFilters)));
		assertFalse(session.containsAllMessages(Arrays.asList(allMatchesTopicFilters)));
		
		// cache only
		session = createSessionFillCache();
		//--- check exception
		try {
			session.retainAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		//--- check data
		assertFalse(session.containsAllMessages(Arrays.asList(noMatchesTopicFilters)));
		assertFalse(session.containsAllMessages(Arrays.asList(mixedMatchesTopicFilters)));
		assertTrue(session.containsAllMessages(Arrays.asList(allMatchesTopicFilters)));
		
		// buffer only
		session = createSessionFillBuffer();
		//--- check exception
		try {
			session.retainAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		//--- check data
		assertFalse(session.containsAllMessages(Arrays.asList(noMatchesTopicFilters)));
		assertFalse(session.containsAllMessages(Arrays.asList(mixedMatchesTopicFilters)));
		assertTrue(session.containsAllMessages(Arrays.asList(allMatchesTopicFilters)));
		
		// both
		session = createSessionFillAll();
		//--- check exception
		try {
			session.retainAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		//--- check data
		assertFalse(session.containsAllMessages(Arrays.asList(noMatchesTopicFilters)));
		assertFalse(session.containsAllMessages(Arrays.asList(mixedMatchesTopicFilters)));
		assertTrue(session.containsAllMessages(Arrays.asList(allMatchesTopicFilters)));
	}

	@Test
	public void testRemoveMatchedPatternsFromMapByList() {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(new MqttConnectionParams());
		assertFalse(session.isConnected());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		Map<String, Pattern> map;
		
		// empty - empty
		map = createPatternMap();
		assertTrue(map.isEmpty());
		assertTrue(session.removeMatchedPatternsFromMapByList(0, map));
		
		// empty - no empty
		map = createPatternMap(mixedMatchesTopicFilters);
		assertFalse(map.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.removeMatchedPatternsFromMapByList(0, map));
		
		// set data
		session = createSessionFillBuffer();

		// check remove (has no matches topics)
		assertFalse(session.removeMatchedPatternsFromMapByList(0, map));
		assertFalse(map.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// check remove (has only matches topics)
		map = createPatternMap(allMatchesTopicFilters);
		assertTrue(session.removeMatchedPatternsFromMapByList(0, map));
		assertTrue(map.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// check remove (has only matches topics, set begin index)
		map = createPatternMap(allMatchesTopicFilters);
		assertFalse(session.removeMatchedPatternsFromMapByList(10, map));
		assertFalse(map.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
	}

	@Test
	public void testRetainMessage() {
		final String noMatch1 = "hoge/hoge";
		final String noMatch2 = "hoge/#";
		final String matched1 = "/mqtt/unittest/arrived/test/3";
		final String matched2 = "/mqtt/unittest/sub/#";
		MqttBufferedSessionImpl session;
		int numMatched1Elements = 1;
		int numMatched2Elements = 0;
		{
			Pattern pat = MqttBufferedSessionImpl.createTopicPattern(matched2);
			for (MqttArrivedMessage msg : testMessageData) {
				if (pat.matcher(msg.getTopic()).matches()) {
					numMatched2Elements++;
				}
			}
		}
		
		// empty
		session = createSessionEmpty();
		//--- check exception
		try {
			session.retainMessage(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			session.retainMessage("");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		//--- check data
		assertFalse(session.containsMessage(noMatch1));
		assertFalse(session.containsMessage(noMatch2));
		assertFalse(session.containsMessage(matched1));
		assertFalse(session.containsMessage(matched2));
		assertFalse(session.retainMessage(noMatch2));
		assertFalse(session.retainMessage(noMatch2));
		assertFalse(session.retainMessage(matched1));
		assertFalse(session.retainMessage(matched2));
		
		// cache only
		session = createSessionFillCache();
		//--- check exception
		try {
			session.retainMessage(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			session.retainMessage("");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		//--- check data
		assertFalse(session.containsMessage(noMatch1));
		assertTrue(session.retainMessage(noMatch1));
		assertTrue(session._listBuffer.isEmpty());
		//---
		session = createSessionFillCache();
		assertFalse(session.containsMessage(noMatch2));
		assertTrue(session.retainMessage(noMatch2));
		assertTrue(session._listBuffer.isEmpty());
		//---
		session = createSessionFillCache();
		assertTrue(session.containsMessage(matched1));
		assertTrue(session.retainMessage(matched1));
		assertEquals(session._listBuffer.size(), numMatched1Elements);
		//---
		session = createSessionFillCache();
		assertTrue(session.containsMessage(matched2));
		assertTrue(session.retainMessage(matched2));
		assertEquals(session._listBuffer.size(), numMatched2Elements);
		
		// buffer only
		session = createSessionFillBuffer();
		//--- check exception
		try {
			session.retainMessage(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			session.retainMessage("");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		//--- check data
		assertFalse(session.containsMessage(noMatch1));
		assertTrue(session.retainMessage(noMatch1));
		assertTrue(session._listBuffer.isEmpty());
		//---
		session = createSessionFillBuffer();
		assertFalse(session.containsMessage(noMatch2));
		assertTrue(session.retainMessage(noMatch2));
		assertTrue(session._listBuffer.isEmpty());
		//---
		session = createSessionFillBuffer();
		assertTrue(session.containsMessage(matched1));
		assertTrue(session.retainMessage(matched1));
		assertEquals(session._listBuffer.size(), numMatched1Elements);
		//---
		session = createSessionFillBuffer();
		assertTrue(session.containsMessage(matched2));
		assertTrue(session.retainMessage(matched2));
		assertEquals(session._listBuffer.size(), numMatched2Elements);
		
		// both
		session = createSessionFillAll();
		//--- check exception
		try {
			session.retainMessage(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			session.retainMessage("");
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		//--- check data
		assertFalse(session.containsMessage(noMatch1));
		assertTrue(session.retainMessage(noMatch1));
		assertTrue(session._listBuffer.isEmpty());
		//---
		session = createSessionFillAll();
		assertFalse(session.containsMessage(noMatch2));
		assertTrue(session.retainMessage(noMatch2));
		assertTrue(session._listBuffer.isEmpty());
		//---
		session = createSessionFillAll();
		assertTrue(session.containsMessage(matched1));
		assertTrue(session.retainMessage(matched1));
		assertEquals(session._listBuffer.size(), numMatched1Elements);
		//---
		session = createSessionFillAll();
		assertTrue(session.containsMessage(matched2));
		assertTrue(session.retainMessage(matched2));
		assertEquals(session._listBuffer.size(), numMatched2Elements);
	}

	@Test
	public void testRetainAllMessages() {
		MqttBufferedSessionImpl session;
		int numMatched1Elements = countMatches(testMessageData, mixedMatchesTopicFilters);
		int numMatched2Elements = countMatches(testMessageData, allMatchesTopicFilters);
		
		// empty
		session = createSessionEmpty();
		//--- check exception
		try {
			session.retainAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		//--- check data
		assertFalse(session.retainAllMessages(Arrays.asList(noMatchesTopicFilters)));
		assertFalse(session.retainAllMessages(Arrays.asList(mixedMatchesTopicFilters)));
		assertFalse(session.retainAllMessages(Arrays.asList(allMatchesTopicFilters)));
		
		// cache only
		session = createSessionFillCache();
		//--- check exception
		try {
			session.retainAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		//--- check data
		session = createSessionFillCache();
		assertTrue(session.retainAllMessages(Arrays.asList(noMatchesTopicFilters)));
		assertTrue(session.isMessageEmpty());
		session = createSessionFillCache();
		assertTrue(session.retainAllMessages(Arrays.asList(mixedMatchesTopicFilters)));
		assertEquals(session.getMessageCount(), numMatched1Elements);
		session = createSessionFillCache();
		assertTrue(session.retainAllMessages(Arrays.asList(allMatchesTopicFilters)));
		assertEquals(session.getMessageCount(), numMatched2Elements);
		
		// buffer only
		session = createSessionFillBuffer();
		//--- check exception
		try {
			session.retainAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		//--- check data
		session = createSessionFillBuffer();
		assertTrue(session.retainAllMessages(Arrays.asList(noMatchesTopicFilters)));
		assertTrue(session.isMessageEmpty());
		session = createSessionFillBuffer();
		assertTrue(session.retainAllMessages(Arrays.asList(mixedMatchesTopicFilters)));
		assertEquals(session.getMessageCount(), numMatched1Elements);
		session = createSessionFillBuffer();
		assertTrue(session.retainAllMessages(Arrays.asList(allMatchesTopicFilters)));
		assertEquals(session.getMessageCount(), numMatched2Elements);
		
		// both
		session = createSessionFillAll();
		//--- check exception
		try {
			session.retainAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.retainAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		//--- check data
		session = createSessionFillAll();
		assertTrue(session.retainAllMessages(Arrays.asList(noMatchesTopicFilters)));
		assertTrue(session.isMessageEmpty());
		session = createSessionFillAll();
		assertTrue(session.retainAllMessages(Arrays.asList(mixedMatchesTopicFilters)));
		assertEquals(session.getMessageCount(), numMatched1Elements);
		session = createSessionFillAll();
		assertTrue(session.retainAllMessages(Arrays.asList(allMatchesTopicFilters)));
		assertEquals(session.getMessageCount(), numMatched2Elements);
	}

	@Test
	public void testCopyMessageNoWait() {
		// empty
		MqttBufferedSessionImpl session = createSessionEmpty();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.copyMessageNoWait());
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// cache only
		session = createSessionFillCache();
		assertFalse(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertTrue(session.copyMessageNoWait());
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// buffer only
		session = createSessionFillBuffer();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertFalse(session.copyMessageNoWait());
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// both
		session = createSessionFillAll();
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertTrue(session.copyMessageNoWait());
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
	}

	@Test
	public void testCopyMessageAndWait() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.copyMessageAndWait(200L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// empty
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.copyMessageAndWait(200L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// cache only
		session = createSessionFillCache();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertTrue(session.copyMessageAndWait(200L));
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// buffer only
		session = createSessionFillBuffer();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertFalse(session.copyMessageAndWait(200L));
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// both
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertTrue(session.copyMessageAndWait(200L));
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// check copy
		session = createSessionEmpty();
		session._disconnected = false;
		Thread th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (; session.copyMessageAndWait(200L); );
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// check copy intinite
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			assertTrue(session.copyMessageAndWait(0L));
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
	}

	@Test
	public void testGetMessageNoWait() {
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.getMessageNoWait(false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMessageNoWait(true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// empty
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMessageNoWait(false));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMessageNoWait(true));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// both
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		MqttArrivedMessage retmsg = session.getMessageNoWait(false);
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		assertEquals(retmsg.getTopic(), testMessageData[0].getTopic());
		assertTrue(Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
		
		// both with remove
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMessageData.length; i++) {
			retmsg = session.getMessageNoWait(true);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testGetMessageAndWait() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.getMessageAndWait(200L, false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMessageAndWait(200L, true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// empty
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMessageAndWait(200L, false));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMessageAndWait(200L, true));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		Thread th;
		
		// check copy without remove (消さないから、スレッド終了より早いので注意)
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessageAndWait(200L, false);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[0].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessageAndWait(0L, false);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[0].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// check copy with remove
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessageAndWait(200L, true);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessageAndWait(0L, true);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testGetMatchedMessageNoWait() {
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		String topicString = testMessageData[0].getTopic();
		Pattern topicPattern;
		if (MqttBufferedSessionImpl.isFilterPatternTopic(topicString))
			topicPattern = MqttBufferedSessionImpl.createTopicPattern(testMessageData[0].getTopic());
		else
			topicPattern = MqttBufferedSessionImpl._emptyPattern;
		try {
			session.getMatchedMessageNoWait(topicString, topicPattern, false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMatchedMessageNoWait(topicString, topicPattern, true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// empty
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMatchedMessageNoWait(topicString, topicPattern, false));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMatchedMessageNoWait(topicString, topicPattern, true));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// both wihtout remove
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			MqttArrivedMessage retmsg = session.getMatchedMessageNoWait(data.topicString, data.topicPattern, false);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// both with remove
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			MqttArrivedMessage retmsg = session.getMatchedMessageNoWait(data.topicString, data.topicPattern, true);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testGetMatchedMessageAndWait() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		String topicString = testMessageData[0].getTopic();
		Pattern topicPattern;
		if (MqttBufferedSessionImpl.isFilterPatternTopic(topicString))
			topicPattern = MqttBufferedSessionImpl.createTopicPattern(testMessageData[0].getTopic());
		else
			topicPattern = MqttBufferedSessionImpl._emptyPattern;
		try {
			session.getMatchedMessageAndWait(topicString, topicPattern, 200L, false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMatchedMessageAndWait(topicString, topicPattern, 200L, true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// empty
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMatchedMessageAndWait(topicString, topicPattern, 200L, false));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMatchedMessageAndWait(topicString, topicPattern, 200L, true));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		Thread th;
		
		// both wihtout remove (消さないから、スレッド終了より早いので注意)
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			MqttArrivedMessage retmsg = session.getMatchedMessageAndWait(data.topicString, data.topicPattern, 200L, false);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			if (data.message != null) {
				MqttArrivedMessage retmsg = session.getMatchedMessageAndWait(data.topicString, data.topicPattern, 0L, false);
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
				try {
					Thread.sleep(110L);
				} catch (InterruptedException ex) {}
			}
		}
		th.join();
		//assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		
		// both with remove
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			MqttArrivedMessage retmsg = session.getMatchedMessageAndWait(data.topicString, data.topicPattern, 200L, true);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			if (data.message != null) {
				MqttArrivedMessage retmsg = session.getMatchedMessageAndWait(data.topicString, data.topicPattern, 0L, true);
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testGetMessage() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.getMessage(0L, false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMessage(0L, true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMessage(200L, false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMessage(200L, true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMessage(false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getMessage(true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		Thread th;
		
		// empty without remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMessage(0L, false));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMessage(200L, false));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.getMessage(false);
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// empty with remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMessage(0L, true));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getMessage(200L, true));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.getMessage(true);
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// check without remove
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessage(0L, false);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[0].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessage(200L, false);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[0].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessage(false);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[0].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// check with remove
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessage(0L, true);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessage(200L, true);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.getMessage(true);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testWaitMessage() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.waitMessage(0L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.waitMessage(200L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.waitMessage();
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		Thread th;
		
		// empty without remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.waitMessage(0L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.waitMessage(200L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.waitMessage();
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// check without remove
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMessageData.length; i++) {
			assertTrue(session.waitMessage(0L));
			MqttArrivedMessage retmsg = session.getMessage(0L, false);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[0].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			assertTrue(session.waitMessage(200L));
			MqttArrivedMessage retmsg = session.getMessage(0L, false);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[0].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			session.waitMessage();
			MqttArrivedMessage retmsg = session.getMessage(0L, false);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[0].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[0].getPayload().toArray()));
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
	}

	@Test
	public void testPopMessage() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.popMessage(0L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.popMessage(200L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.popMessage();
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		Thread th;
		
		// empty with remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.popMessage(0L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.popMessage(200L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.popMessage();
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// check with remove
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.popMessage(0L);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.popMessage(200L);
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMessageData.length; i++) {
			MqttArrivedMessage retmsg = session.popMessage();
			assertEquals(toIndexMessage(i), retmsg.getTopic(), testMessageData[i].getTopic());
			assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), testMessageData[i].getPayload().toArray()));
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testGetFilteredMessageString() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.getFilteredMessage(testMessageData[0].getTopic(), 0L, false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getFilteredMessage(testMessageData[0].getTopic(), 0L, true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getFilteredMessage(testMessageData[0].getTopic(), 200L, false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getFilteredMessage(testMessageData[0].getTopic(), 200L, true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getFilteredMessage(testMessageData[0].getTopic(), false);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.getFilteredMessage(testMessageData[0].getTopic(), true);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// check illegal arguments
		session._disconnected = false;
		try {
			session.getFilteredMessage(null, 0L, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.getFilteredMessage(null, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.getFilteredMessage("", 0L, false);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		try {
			session.getFilteredMessage("", false);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		Thread th;
		
		// empty without remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getFilteredMessage(testMessageData[0].getTopic(), 0L, false));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getFilteredMessage(testMessageData[0].getTopic(), 200L, false));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.getFilteredMessage(testMessageData[0].getTopic(), false);
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// empty with remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getFilteredMessage(testMessageData[0].getTopic(), 0L, true));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.getFilteredMessage(testMessageData[0].getTopic(), 200L, true));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.getFilteredMessage(testMessageData[0].getTopic(), true);
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// both wihtout remove (消さないから、スレッド終了より早いので注意)
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, 0L, false);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, 200L, false);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			if (data.message != null) {
				MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, false);
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
				try {
					Thread.sleep(110L);
				} catch (InterruptedException ex) {}
			}
		}
		th.join();
		//assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		
		// both with remove
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, 0L, true);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, 200L, true);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			if (data.message != null) {
				MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, true);
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testWaitMessageString() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.waitMessage(testMessageData[0].getTopic(), 0L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.waitMessage(testMessageData[0].getTopic(), 200L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.waitMessage(testMessageData[0].getTopic());
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// check illegal arguments
		session._disconnected = false;
		try {
			session.waitMessage(null, 0L);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.waitMessage("", 0L);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		Thread th;
		
		// empty without remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.waitMessage(testMessageData[0].getTopic(), 0L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.waitMessage(testMessageData[0].getTopic(), 200L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.waitMessage(testMessageData[0].getTopic());
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// both wihtout remove (消さないから、スレッド終了より早いので注意)
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			boolean retwait = session.waitMessage(data.topicString, 0L);
			MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, 0L, false);
			if (data.message == null) {
				assertFalse(toIndexMessage(i), retwait);
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertTrue(toIndexMessage(i), retwait);
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			boolean retwait = session.waitMessage(data.topicString, 200L);
			MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, 0L, false);
			if (data.message == null) {
				assertFalse(toIndexMessage(i), retwait);
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertTrue(toIndexMessage(i), retwait);
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
			try {
				Thread.sleep(110L);
			} catch (InterruptedException ex) {}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_noremove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_noremove[i];
			if (data.message != null) {
				session.waitMessage(data.topicString);
				MqttArrivedMessage retmsg = session.getFilteredMessage(data.topicString, 0L, false);
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
				try {
					Thread.sleep(110L);
				} catch (InterruptedException ex) {}
			}
		}
		th.join();
		//assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
	}

	@Test
	public void testPopFilteredMessageString() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.popFilteredMessage(testMessageData[0].getTopic(), 0L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.popFilteredMessage(testMessageData[0].getTopic(), 200L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.popFilteredMessage(testMessageData[0].getTopic());
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// check illegal arguments
		session._disconnected = false;
		try {
			session.popFilteredMessage(null, 0L);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.popFilteredMessage("", 0L);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		Thread th;
		
		// empty with remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.popFilteredMessage(testMessageData[0].getTopic(), 0L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertNull(session.popFilteredMessage(testMessageData[0].getTopic(), 200L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.popFilteredMessage(testMessageData[0].getTopic());
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// both with remove
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			MqttArrivedMessage retmsg = session.popFilteredMessage(data.topicString, 0L);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- timeout
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			MqttArrivedMessage retmsg = session.popFilteredMessage(data.topicString, 200L);
			if (data.message == null) {
				assertNull(toIndexMessage(i), retmsg);
			} else {
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		//--- infinit
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMessageThread(session);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		for (int i = 0; i < testMatchedMessageData_mixed_remove.length; i++) {
			MessageMatchedData data = testMatchedMessageData_mixed_remove[i];
			if (data.message != null) {
				MqttArrivedMessage retmsg = session.popFilteredMessage(data.topicString);
				assertEquals(toIndexMessage(i), retmsg.getTopic(), data.message.getTopic());
				assertTrue(toIndexMessage(i), Arrays.equals(retmsg.getPayload().toArray(), data.message.getPayload().toArray()));
			}
		}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
	}

	@Test
	public void testWaitAllMessagesListOfQ() throws InterruptedException
	{
		// check disconnected
		MqttBufferedSessionImpl session = createSessionEmpty();
		try {
			session.waitAllMessages(Arrays.asList(allMatchesTopicFilters), 0L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.waitAllMessages(Arrays.asList(allMatchesTopicFilters), 200L);
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		try {
			session.waitAllMessages(Arrays.asList(allMatchesTopicFilters));
			fail("MqttRuntimeException is expected.");
		} catch (MqttRuntimeException ex) {}
		
		// check illegal arguments
		session._disconnected = false;
		try {
			session.waitAllMessages(null, 0L);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.waitAllMessages(null, 200L);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.waitAllMessages(null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.waitAllMessages(Arrays.asList(includeNullFilters), 0L);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.waitAllMessages(Arrays.asList(includeNullFilters), 200L);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.waitAllMessages(Arrays.asList(includeNullFilters));
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			session.waitAllMessages(Arrays.asList(includeEmptyFilters), 0L);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		try {
			session.waitAllMessages(Arrays.asList(includeEmptyFilters), 200L);
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		try {
			session.waitAllMessages(Arrays.asList(includeEmptyFilters));
			fail("IllegalArgumentException is expected.");
		} catch (IllegalArgumentException ex) {}
		
		Thread th;
		
		// empty without remove
		session = createSessionEmpty();
		session._disconnected = false;
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.waitAllMessages(Arrays.asList(allMatchesTopicFilters), 0L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		assertFalse(session.waitAllMessages(Arrays.asList(allMatchesTopicFilters), 200L));
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th = new SessionInterruptThread(session);
		th.start();
		try {
			session.waitAllMessages(Arrays.asList(allMatchesTopicFilters));
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		
		// both wihtout remove (消さないから、スレッド終了より早いので注意)
		//--- no wait
		session = createSessionFillAll();
		session._disconnected = false;
		assertFalse(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertTrue(session.waitAllMessages(Arrays.asList(allMatchesTopicFilters), 0L));
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		assertFalse(session.waitAllMessages(Arrays.asList(mixedMatchesTopicFilters), 0L));
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		assertFalse(session.waitAllMessages(Arrays.asList(noMatchesTopicFilters), 0L));
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testMessageData.length);
		//--- timeout(all matches)
		long timeout = testAllMatchesMessageData.length * 110;
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMatchedMessageThread(session, false);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		assertTrue(session.waitAllMessages(Arrays.asList(allMatchesTopicFilters), timeout));
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testAllMatchesMessageData.length);
		//--- timeout(include no matches)
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMatchedMessageThread(session, false);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		assertFalse(session.waitAllMessages(Arrays.asList(mixedMatchesTopicFilters), timeout));
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testAllMatchesMessageData.length);
		//--- infinit(all matches)
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMatchedMessageThread(session, false);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		session.waitAllMessages(Arrays.asList(allMatchesTopicFilters));
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testAllMatchesMessageData.length);
		//--- infinit(include no matches)
		session = createSessionEmpty();
		session._disconnected = false;
		th = new SessionPseudoMatchedMessageThread(session, true);
		assertTrue(session._listCache.isEmpty());
		assertTrue(session._listBuffer.isEmpty());
		th.start();
		try {
			session.waitAllMessages(Arrays.asList(mixedMatchesTopicFilters));
			fail("InterruptedException is expected.");
		} catch (InterruptedException ex) {}
		th.join();
		assertTrue(session._listCache.isEmpty());
		assertFalse(session._listBuffer.isEmpty());
		assertEquals(session._listBuffer.size(), testAllMatchesMessageData.length);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static class SessionControlThread extends Thread {
		public final MqttBufferedSessionImpl _session;
		
		public SessionControlThread(MqttBufferedSessionImpl targetSession) {
			super("Session control thread");
			_session = targetSession;
		}
	}
	
	static class SessionInterruptThread extends SessionControlThread
	{
		final Thread _mainThread;
		
		public SessionInterruptThread(MqttBufferedSessionImpl targetSession) {
			super(targetSession);
			_mainThread = Thread.currentThread();
		}
		
		public void run() {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException ex) {}
			//--- interrupt
			_mainThread.interrupt();
		}
	}
	
	static class SessionPseudoMessageThread extends SessionControlThread
	{
		public SessionPseudoMessageThread(MqttBufferedSessionImpl targetSession) {
			super(targetSession);
		}
		
		public void run() {
			for (MqttArrivedMessage msg : testMessageData) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException ex) {}
				MqttArrivedMessage newmsg = new MqttArrivedMessage(msg.getTopic(),
												msg.getPayload().getData(), msg.getPayload().getOffset(), msg.getPayload().getLength(),
												msg.getQos(), msg.isRetained(), msg.isDuplicated());
				try {
					_session.onMessageArrived(newmsg);
				} catch (Exception ex) {
					_session.onConnectionLost(ex);
				}
			}
		}
	}
	
	static class SessionPseudoMatchedMessageThread extends SessionControlThread
	{
		final Thread _mainThread;
		
		public SessionPseudoMatchedMessageThread(MqttBufferedSessionImpl targetSession, boolean interrupt) {
			super(targetSession);
			if (interrupt) {
				_mainThread = Thread.currentThread();
			} else {
				_mainThread = null;
			}
		}
		
		public void run() {
			for (MqttArrivedMessage msg : testAllMatchesMessageData) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException ex) {}
				MqttArrivedMessage newmsg = new MqttArrivedMessage(msg.getTopic(),
						msg.getPayload().getData(), msg.getPayload().getOffset(), msg.getPayload().getLength(),
						msg.getQos(), msg.isRetained(), msg.isDuplicated());
				try {
					_session.onMessageArrived(newmsg);
				} catch (Exception ex) {
					_session.onConnectionLost(ex);
				}
			}
			
			if (_mainThread != null) {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException ex) {}
				//--- interrupt
				_mainThread.interrupt();
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
