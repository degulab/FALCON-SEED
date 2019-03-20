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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
package ssac.aadl.runtime.mqtt;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class MqttCsvParameterTest
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String[] illegalServerURIs = {
		"http://www.piecake.com:1883",
		"://192.168.101.1:",
		"tcp://192.168.101.1:65543245",
	};
	
	static protected final String[][] validServerURIs = {
		{"127.0.0.1:", "tcp://127.0.0.1:1883"},
		{"localhost", "tcp://localhost:1883"},
		{"192.168.101.1", "tcp://192.168.101.1:1883"},
		{"tcp://localhost", "tcp://localhost:1883"},
		{"ssl://user:password@localhost:1234", "ssl://user:password@localhost:1234"},
	};
	
	static protected final String[] illegalClientIDs = {
		"test|ClientID",
		"123456789012345678901234",
	};
	
	static protected final String[] validClientIDs = {
		"testClientID",
		"12345678901234567890123",
	};
	
	static protected final String[] invalidTopics = {
		"/", "hoge/hoge//hoge/hoge", "hogehoge/hoge/hho/",
		"hoge+hoge", "hoge#hoge", "##", "++",
		"#hoge", "+hoge", "/hoge/#hoge", "/hoge/+hoge",
	};
	
	static protected final String[] validTopicFilters = {
		"#", "+", "hoge/#", "hoge/+",
		"/#", "/+", "/hoge/+", "/hoge/#",
		"hoge/+/hoge/+/+/#", "+/hoge/+/#",
	};
	
	static protected final String[] validTopics = {
		"hoge", "/hoge", "hoge/hoge", "hoge/hoge/hoge/hoge/hoge",
	};
	
	static protected final String DEFAULT_SERVERURI = "localhost";
	static protected final String DEFAULT_CLIENTID  = "testClientID";
	
	static protected final String[] illegalCsvStrings = {
		"http://www.piecake.com:1883",
		"\"://192.168.101.1:\",/hoge,/hoge/+",
		"\"tcp://192.168.101.1:65543245|qos=2|clientid=hoge\"",
		"localhost|qo=2|clientid=hoge,/hoge,+/hoge/#",
		"localhost|qos=2|clintid=hoge,/hoge,+/hoge/#",
		"localhost|qos=|clientid=hoge,/hoge,+/hoge/#",
		"localhost|qos=2|clientid=,/hoge,+/hoge/#",
		"localhost|qos=-1|clientid=hoge,/hoge,+/hoge/#",
		"localhost|qos=3|clientid=hoge,/hoge,+/hoge/#",
		"localhost|qos=2|clientid=hoge|hoge,/hoge,+/hoge/#",
		"localhost|qos=2|clientid=123456789012345678901234,/hoge,+/hoge/#",
		"localhost|qos=2|clientid=hoge,/hoge/,+/hoge/#",
		"localhost|qos=2|clientid=hoge,/hoge,+/hoge/#hoge",
	};
	
	static protected final Object[][] validCsvStringsWithWildcard = {
		{
			"localhost|qos=0|clientID=test1,/hoge,#,+/hoge/+/hoge/#",
			new MqttCsvParameter("localhost", "test1", 0, Arrays.asList("/hoge","#","+/hoge/+/hoge/#"), true),
		},
		{
			"\"localhost|qos=0|clientID=test1\",/hoge,#,\"+/\"\"hoge\"\"/+/hoge/#",
			new MqttCsvParameter("localhost", "test1", 0, Arrays.asList("/hoge","#","+/\"hoge\"/+/hoge/#"), true)
		},
		{
			"localhost|qos=0|clientID=test1|clientID=test2|qos=2|clientID=test3,/hoge,#,\"+/hoge/+/ho,ge/#\"",
			new MqttCsvParameter("localhost", "test3", 2, Arrays.asList("/hoge","#","+/hoge/+/ho,ge/#"), true)
		},
	};
	
	static protected final Object[][] validCsvStringNoWildcard = {
		{
			"localhost|qos=0|clientID=test1,/hoge,/hoge/hoge/sage",
			new MqttCsvParameter("localhost", "test1", 0, Arrays.asList("/hoge","/hoge/hoge/sage"), false),
		},
		{
			"\"localhost|qos=0|clientID=test1\",/hoge,\"hoge/\"\"age\"\"/hoge",
			new MqttCsvParameter("localhost", "test1", 0, Arrays.asList("/hoge","hoge/\"age\"/hoge"), false)
		},
		{
			"localhost|qos=0|clientID=test1|clientID=test2|qos=2|clientID=test3,/hoge,,\"hoge/sage/ho,ge\"",
			new MqttCsvParameter("localhost", "test3", 2, Arrays.asList("/hoge","hoge/sage/ho,ge"), false)
		},
	};
	
	static protected final String[][] validConvertCsvStrings = {
		{
			"localhost",
			"tcp://localhost:1883|qos=1"
		},
		{
			"localhost:1212,/hoge,#,\"+/\"\"hoge\"\"/+/ho,ge/#\"",
			"tcp://localhost:1212|qos=1,/hoge,#,\"+/\"\"hoge\"\"/+/ho,ge/#\""
		},
		{
			"localhost|qos=0|clientID=test1|clientID=test2|qos=2|clientID=test3,/hoge,#,\"+/hoge/+/ho,ge/#\"",
			"tcp://localhost:1883|qos=2|clientID=test3,/hoge,#,\"+/hoge/+/ho,ge/#\""
		},
		{
			"\"localhost|qos=0|clientID=test1\",/hoge,\"hoge/\"\"age\"\"/hoge",
			"tcp://localhost:1883|qos=0|clientID=test1,/hoge,\"hoge/\"\"age\"\"/hoge\""
		},
	};
	
	static protected final String[][] validResultStrings = {
		{
			"localhost",
			"ssac.aadl.runtime.mqtt.MqttCsvParameter[uri=\"tcp://localhost:1883\", QoS=1, clientID=null, topics=[]]"
		},
		{
			"localhost:1212,/hoge,#,\"+/\"\"hoge\"\"/+/ho,ge/#\"",
			"ssac.aadl.runtime.mqtt.MqttCsvParameter[uri=\"tcp://localhost:1212\", QoS=1, clientID=null, topics=[/hoge, #, +/\"hoge\"/+/ho,ge/#]]"
		},
		{
			"localhost|qos=0|clientID=test1|clientID=test2|qos=2|clientID=test3,/hoge,#,\"+/hoge/+/ho,ge/#\"",
			"ssac.aadl.runtime.mqtt.MqttCsvParameter[uri=\"tcp://localhost:1883\", QoS=2, clientID=\"test3\", topics=[/hoge, #, +/hoge/+/ho,ge/#]]"
		},
		{
			"\"localhost|qos=0|clientID=test1\",/hoge,\"hoge/\"\"age\"\"/hoge",
			"ssac.aadl.runtime.mqtt.MqttCsvParameter[uri=\"tcp://localhost:1883\", QoS=0, clientID=\"test1\", topics=[/hoge, hoge/\"age\"/hoge]]"
		},
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------
	
	@Test
	public void testParseServerURIString() {
		// check null
		try {
			MqttConnectionParams.parseServerURI(null);
			fail("Expecting NullPointerException.");
		}
		catch (NullPointerException ex) {
			assert(true);
		}
		
		// check empty
		try {
			MqttConnectionParams.parseServerURI("");
			fail("Expecting IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("parseServerURI(\"\") : " + ex);
		}
		
		// illegal server URI
		for (String str : illegalServerURIs) {
			try {
				MqttConnectionParams.parseServerURI(str);
				fail("parseServerURI(" + str + ") : Expecting IllegalArgumentException");
			}
			catch (IllegalArgumentException ex) {
				System.out.println("parseServerURI(" + str + ") : " + ex);
			}
		}
		
		// valid URI
		for (String[] strpair : validServerURIs) {
			try {
				URI uri = MqttConnectionParams.parseServerURI(strpair[0]);
				assertEquals(strpair[1], uri.toString());
			}
			catch (IllegalArgumentException ex) {
				fail("parseServerURI(" + strpair[0] + ") : Unexpected " + ex);
			}
		}
	}

	@Test
	public void testMqttCsvParameterStringStringIntCollectionOfStringBoolean() {
		MqttCsvParameter param;
		
		// check server URI
		//--- check null
		try {
			new MqttCsvParameter(null, null, 0, null, true);
			fail("Expecting NullPointerException.");
		}
		catch (NullPointerException ex) {
			assert(true);
		}
		//--- check empty
		try {
			new MqttCsvParameter("", null, 0, null, true);
			fail("Expecting IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("new MqttCsvParameter(\"\", null, 0, null, true) : " + ex);
		}
		//--- illegal server URI
		for (String str : illegalServerURIs) {
			try {
				new MqttCsvParameter(str, null, 0, null, true);
				fail("new MqttCsvParameter(" + str + ", null, 0, null, true) : Expecting IllegalArgumentException");
			}
			catch (IllegalArgumentException ex) {
				System.out.println("new MqttCsvParameter(" + str + ", null, 0, null, true) : " + ex);
			}
		}
		//--- valid URI
		for (String[] strpair : validServerURIs) {
			try {
				param = new MqttCsvParameter(strpair[0], null, 0, null, true);
				assertEquals(strpair[1], param._serverURI);
			}
			catch (IllegalArgumentException ex) {
				fail("new MqttCsvParameter(" + strpair[0] + ", null, 0, null, true) : Unexpected " + ex);
			}
		}
		
		// check Client ID
		//--- null
		param = new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, null, true);
		assertNull(param._clientID);
		//--- empty
		param = new MqttCsvParameter(DEFAULT_SERVERURI, "", 0, null, true);
		assertNull(param._clientID);
		//--- illegal
		for (String str : illegalClientIDs) {
			try {
				new MqttCsvParameter(DEFAULT_SERVERURI, str, 0, null, true);
				fail("new MqttCsvParameter(DEFAULT_SERVERURI, " + str + ", 0, null, true) : Expecting IllegalArgumentException");
			}
			catch (IllegalArgumentException ex) {
				System.out.println("new MqttCsvParameter(DEFAULT_SERVERURI, " + str + ", 0, null, true) : " + ex);
			}
		}
		//--- valid
		for (String str : validClientIDs) {
			param = new MqttCsvParameter(DEFAULT_SERVERURI, str, 0, null, true);
			assertEquals(str, param._clientID);
		}
		
		// check QOS
		for (int qos = -1; qos < 4; ++qos) {
			if (qos < 0 || qos > 2) {
				try {
					new MqttCsvParameter(DEFAULT_SERVERURI, null, -1, null, true);
					fail("new MqttCsvParameter(DEFAULT_SERVERURI, null, " + qos + ", null, true) : Expecting IllegalArgumentException");
				}
				catch (IllegalArgumentException ex) {
					System.out.println("new MqttCsvParameter(DEFAULT_SERVERURI, null, " + qos + ", null, true) : " + ex);
				}
			}
			else {
				param = new MqttCsvParameter(DEFAULT_SERVERURI, null, qos, null, true);
				assertEquals(qos, param._qos);
			}
		}
		
		// check Topics
		//--- invalid topic filters
		for (String str : invalidTopics) {
			try {
				new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, Arrays.asList(str), true);
				fail("new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, [" + str + "], true) : Expecting IllegalArgumentException");
			} catch (IllegalArgumentException ex) {
				System.out.println("new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, [" + str + "], true) : " + ex);
			}
			try {
				new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, Arrays.asList(str), false);
				fail("new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, [" + str + "], false) : Expecting IllegalArgumentException");
			} catch (IllegalArgumentException ex) {
				System.out.println("new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, [" + str + "], false) : " + ex);
			}
		}
		//--- invalid wildcard topics
		for (String str : validTopicFilters) {
			try {
				new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, Arrays.asList(str), false);
				fail("new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, [" + str + "], false) : Expecting IllegalArgumentException");
			} catch (IllegalArgumentException ex) {
				System.out.println("new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, [" + str + "], false) : " + ex);
			}
		}
		//--- valid topic filters
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(validTopicFilters));
		param = new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, list, true);
		assertEquals(Arrays.asList(validTopicFilters), param._topics);
		//--- valid topics
		list = new ArrayList<String>(Arrays.asList(validTopics));
		param = new MqttCsvParameter(DEFAULT_SERVERURI, null, 0, list, false);
		assertEquals(Arrays.asList(validTopics), param._topics);
	}

	@Test
	public void testMqttCsvParameterStringBooleanInt() {
		MqttCsvParameter param;
		MqttCsvParameter ans;
		// illegal csv string
		for (String str : illegalCsvStrings) {
			try {
				new MqttCsvParameter(str, true, 2);
				fail("new MqttCsvParameter(" + str + ", true, 2) : Expecting IllegalArgumentException");
			} catch (IllegalArgumentException ex) {
				System.out.println("new MqttCsvParameter(" + str + ", true, 2) : " + ex);
			}
			try {
				new MqttCsvParameter(str, false, 2);
				fail("new MqttCsvParameter(" + str + ", false, 2) : Expecting IllegalArgumentException");
			} catch (IllegalArgumentException ex) {
				System.out.println("new MqttCsvParameter(" + str + ", false, 2) : " + ex);
			}
		}
		// invalid wildcard
		for (Object[] data : validCsvStringsWithWildcard) {
			String str = data[0].toString();
			try {
				new MqttCsvParameter(str, false, 2);
				fail("new MqttCsvParameter(" + str + ", false, 2) : Expecting IllegalArgumentException");
			} catch (IllegalArgumentException ex) {
				System.out.println("new MqttCsvParameter(" + str + ", false, 2) : " + ex);
			}
		}
		// valid with wildcard
		for (Object[] data : validCsvStringsWithWildcard) {
			String str = data[0].toString();
			ans = (MqttCsvParameter)data[1];
			param = new MqttCsvParameter(str, true, 2);
			assertEquals(ans, param);
		}
		// valid without wildcard
		for (Object[] data : validCsvStringNoWildcard) {
			String str = data[0].toString();
			ans = (MqttCsvParameter)data[1];
			param = new MqttCsvParameter(str, true, 2);
			assertEquals(ans, param);
			param = new MqttCsvParameter(str, false, 2);
			assertEquals(ans, param);
		}
		// check default qos
		param = new MqttCsvParameter("localhost", true, 2);
		assertNull(param._clientID);
		assertTrue(param._topics.isEmpty());
		assertEquals(2, param._qos);
		//---
		param = new MqttCsvParameter("localhost|qos=0", true, 2);
		assertNull(param._clientID);
		assertTrue(param._topics.isEmpty());
		assertEquals(0, param._qos);
	}

	@Test
	public void testMqttCsvParameterStringBoolean() {
		MqttCsvParameter param;
		MqttCsvParameter ans;
		// valid with wildcard
		for (Object[] data : validCsvStringsWithWildcard) {
			String str = data[0].toString();
			ans = (MqttCsvParameter)data[1];
			param = new MqttCsvParameter(str, true);
			assertEquals(ans, param);
		}
		// valid without wildcard
		for (Object[] data : validCsvStringNoWildcard) {
			String str = data[0].toString();
			ans = (MqttCsvParameter)data[1];
			param = new MqttCsvParameter(str, true);
			assertEquals(ans, param);
			param = new MqttCsvParameter(str, false);
			assertEquals(ans, param);
		}
	}

	@Test
	public void testMqttCsvParameterStringInt() {
		MqttCsvParameter param;
		MqttCsvParameter ans;
		// valid with wildcard
		for (Object[] data : validCsvStringsWithWildcard) {
			String str = data[0].toString();
			ans = (MqttCsvParameter)data[1];
			param = new MqttCsvParameter(str, 2);
			assertEquals(ans, param);
		}
		// check default qos
		param = new MqttCsvParameter("localhost", 2);
		assertNull(param._clientID);
		assertTrue(param._topics.isEmpty());
		assertEquals(2, param._qos);
		//---
		param = new MqttCsvParameter("localhost|qos=0", 2);
		assertNull(param._clientID);
		assertTrue(param._topics.isEmpty());
		assertEquals(0, param._qos);
	}

	@Test
	public void testMqttCsvParameterString() {
		MqttCsvParameter param;
		MqttCsvParameter ans;
		// valid with wildcard
		for (Object[] data : validCsvStringsWithWildcard) {
			String str = data[0].toString();
			ans = (MqttCsvParameter)data[1];
			param = new MqttCsvParameter(str);
			assertEquals(ans, param);
		}
		// check default qos
		param = new MqttCsvParameter("localhost");
		assertNull(param._clientID);
		assertTrue(param._topics.isEmpty());
		assertEquals(1, param._qos);
		//---
		param = new MqttCsvParameter("localhost|qos=0");
		assertNull(param._clientID);
		assertTrue(param._topics.isEmpty());
		assertEquals(0, param._qos);
	}

	@Test
	public void testHashCode() {
		MqttCsvParameter param = new MqttCsvParameter(validCsvStringsWithWildcard[0][0].toString());
		MqttCsvParameter ans = (MqttCsvParameter)validCsvStringsWithWildcard[0][1];
		
		int h = 0;
		h = 31 * h + param._serverURI.hashCode();
		h = 31 * h + (param._clientID==null ? 0 : param._clientID.hashCode());
		h = 31 * h + param._qos;
		h = 31 * h + param._topics.hashCode();
		
		assertEquals(h, param.hashCode());
		assertEquals(ans.hashCode(), param.hashCode());
	}

	@Test
	public void testGetMethods() {
		MqttCsvParameter param = new MqttCsvParameter(validCsvStringsWithWildcard[0][0].toString());
		MqttCsvParameter param2 = new MqttCsvParameter(validCsvStringsWithWildcard[1][0].toString());
		MqttCsvParameter ans = (MqttCsvParameter)validCsvStringsWithWildcard[0][1];
		
		assertSame(param._serverURI, param.getServerURI());
		assertSame(param._clientID, param.getClientID());
		assertSame(param._qos, param.getQOS());
		assertSame(param._topics, param.getTopics());
		
		assertEquals(ans._serverURI, param._serverURI);
		assertEquals(ans._clientID, param._clientID);
		assertEquals(ans._qos, param._qos);
		assertEquals(ans._topics, param._topics);
		
		assertEquals(ans.hashCode(), param.hashCode());
		assertTrue(param.equals(ans));
		assertTrue(ans.equals(param));
		
		assertFalse(param.equals(param2));
		assertFalse(param2.equals(param));
	}

	@Test
	public void testGetAvailableClientID() {
		MqttCsvParameter param1 = new MqttCsvParameter("localhost");
		MqttCsvParameter param2 = new MqttCsvParameter("localhost|clientID=testID");
		
		assertNull(param1._clientID);
		assertEquals("testID", param2._clientID);
		
		String strClientID = param1.getAvailableClientID();
		assertNotNull(strClientID);
		assertNull(param1._clientID);
		
		strClientID = param2.getAvailableClientID();
		assertEquals(strClientID, param2._clientID);
	}

	@Test
	public void testToCsvString() {
		MqttCsvParameter param;
		String result;
		
		for (String[] strpair : validConvertCsvStrings) {
			param = new MqttCsvParameter(strpair[0]);
			result = param.toCsvString();
			assertEquals(strpair[1], result);
		}
	}

	@Test
	public void testToString() {
		MqttCsvParameter param;
		String result;
		
		for (String[] strpair : validResultStrings) {
			param = new MqttCsvParameter(strpair[0]);
			result = param.toString();
			assertEquals(strpair[1], result);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
