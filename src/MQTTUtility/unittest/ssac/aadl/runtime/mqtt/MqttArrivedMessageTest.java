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
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Test;

public class MqttArrivedMessageTest
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static private final MqPayload NullPayload = null;
	static private final byte[] NullByteArray  = null;
	static private final byte[] EmptyByteArray = new byte[0];
	static private final String TestStringData1 = "Test string data 1.";
	static private final String TestStringData2 = "テスト・ストリングデータ・２";
	
	static private final String TestTextData = "Sample Text data.\nサンプル・テキスト・データ。\n";
	static private final byte[] TestByteData = TestTextData.getBytes();

	static private final String TestCompareData_1 = "１２３４５";
	static private final String TestCompareData_2 = "12345";
	static private final String TestCompareData_3 = "壱弐参肆伍";
	static private final String TestCompareData = TestCompareData_1 + TestCompareData_2 + TestCompareData_3;
	static private final byte[] ByteCompareData = TestCompareData.getBytes();
	
	static private final String[] CharsetNames = {
		null, "", "SJIS", "MS932", "UTF-8", "EUC-JP",
	};
	
	static private final String basePath = "testdata/unittest/output/MqttArrivedMessageTest_";
	
	final MqPayloadTest.TestData[] validData = {
			MqPayloadTest.makeTestData(null, 0, 0),
			MqPayloadTest.makeTestData(new byte[0], 0, 0),
			MqPayloadTest.makeTestData(ByteCompareData, 0, ByteCompareData.length),
			MqPayloadTest.makeTestData(ByteCompareData, ByteCompareData.length, 0),
			MqPayloadTest.makeTestData(ByteCompareData, 0, TestCompareData_1.getBytes().length),
			MqPayloadTest.makeTestData(ByteCompareData, TestCompareData_1.getBytes().length, TestCompareData_2.getBytes().length),
			MqPayloadTest.makeTestData(ByteCompareData, (TestCompareData_1+TestCompareData_2).getBytes().length, TestCompareData_3.getBytes().length),
	};
	
	final MqPayloadTest.TestData[] invalidData = {
			MqPayloadTest.makeTestData(null, -1, 0),
			MqPayloadTest.makeTestData(null, 10, 0),
			MqPayloadTest.makeTestData(new byte[0], -1, 0),
			MqPayloadTest.makeTestData(new byte[0], 10, 0),
			MqPayloadTest.makeTestData(TestByteData, -1, 0),
			MqPayloadTest.makeTestData(TestByteData, TestByteData.length+1, 0),
	};

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
	public void testMqttArrivedMessageStringMqPayloadIntBooleanBoolean() {
		MqttArrivedMessage msg;
		MqPayload payload = new MqPayload(ByteCompareData);
		
		// test1
		msg = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		assertEquals("", msg.getTopic());
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		assertEquals(0, msg.getQos());
		assertFalse(msg.isRetained());
		assertFalse(msg.isDuplicated());
		
		// test2
		msg = new MqttArrivedMessage(null, MqPayload.EmptyPayload, 1, true, false);
		assertEquals("", msg.getTopic());
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		assertEquals(1, msg.getQos());
		assertTrue(msg.isRetained());
		assertFalse(msg.isDuplicated());
		
		// test3
		msg = new MqttArrivedMessage("", NullPayload, 2, false, true);
		assertEquals("", msg.getTopic());
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		assertEquals(2, msg.getQos());
		assertFalse(msg.isRetained());
		assertTrue(msg.isDuplicated());
		
		// test4
		msg = new MqttArrivedMessage(TestStringData1, MqPayload.EmptyPayload, 1, true, true);
		assertEquals(TestStringData1, msg.getTopic());
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		assertEquals(1, msg.getQos());
		assertTrue(msg.isRetained());
		assertTrue(msg.isDuplicated());
		
		// test5
		msg = new MqttArrivedMessage(TestStringData1, payload, 1, true, true);
		assertEquals(TestStringData1, msg.getTopic());
		assertTrue(payload.exactlyEquals(msg.getPayload()));
		assertEquals(1, msg.getQos());
		assertTrue(msg.isRetained());
		assertTrue(msg.isDuplicated());
	}
	
	@Test
	public void testMqttArrivedMessageStringByteArrayIntBooleanBoolean() {
		MqttArrivedMessage msg;
		MqPayload payload = new MqPayload(ByteCompareData);
		
		// test1
		msg = new MqttArrivedMessage(null, NullByteArray, 0, false, false);
		assertEquals("", msg.getTopic());
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		assertEquals(0, msg.getQos());
		assertFalse(msg.isRetained());
		assertFalse(msg.isDuplicated());
		
		// test2
		msg = new MqttArrivedMessage(null, EmptyByteArray, 1, true, false);
		assertEquals("", msg.getTopic());
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		assertEquals(1, msg.getQos());
		assertTrue(msg.isRetained());
		assertFalse(msg.isDuplicated());
		
		// test3
		msg = new MqttArrivedMessage("", NullByteArray, 2, false, true);
		assertEquals("", msg.getTopic());
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		assertEquals(2, msg.getQos());
		assertFalse(msg.isRetained());
		assertTrue(msg.isDuplicated());
		
		// test4
		msg = new MqttArrivedMessage(TestStringData1, EmptyByteArray, 1, true, true);
		assertEquals(TestStringData1, msg.getTopic());
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		assertEquals(1, msg.getQos());
		assertTrue(msg.isRetained());
		assertTrue(msg.isDuplicated());
		
		// test5
		msg = new MqttArrivedMessage(TestStringData1, ByteCompareData, 1, true, true);
		assertEquals(TestStringData1, msg.getTopic());
		assertTrue(payload.exactlyEquals(msg.getPayload()));
		assertEquals(1, msg.getQos());
		assertTrue(msg.isRetained());
		assertTrue(msg.isDuplicated());
	}
	
	@Test
	public void testMqttArrivedMessageStringByteArrayIntIntIntBooleanBoolean() {
		MqttArrivedMessage msg;
		
		// invalid args
		for (int i = 0; i < invalidData.length; i++) {
			String strmsg = "Index(" + i + ")";
			
			// test 1
			try {
				msg = new MqttArrivedMessage(null, invalidData[i].array, invalidData[i].offset, invalidData[i].length, 0, true, false);
				fail(strmsg + " IndexOutOfBoundsException is expected.");
			} catch (IndexOutOfBoundsException ex) {
				assertTrue(true);
			}
			
			// test 2
			try {
				msg = new MqttArrivedMessage("", invalidData[i].array, invalidData[i].offset, invalidData[i].length, 1, false, true);
				fail(strmsg + " IndexOutOfBoundsException is expected.");
			} catch (IndexOutOfBoundsException ex) {
				assertTrue(true);
			}
			
			// test 3
			try {
				msg = new MqttArrivedMessage(TestStringData1, invalidData[i].array, invalidData[i].offset, invalidData[i].length, 2, true, true);
				fail(strmsg + " IndexOutOfBoundsException is expected.");
			} catch (IndexOutOfBoundsException ex) {
				assertTrue(true);
			}
		}
		
		// valid data
		for (int i = 0; i < validData.length; i++) {
			String strmsg = "Index(" + i + ")";
			MqPayload payload = MqPayloadTest.makePayload(validData[i]);
			
			// test 1
			msg = new MqttArrivedMessage(null, validData[i].array, validData[i].offset, validData[i].length, 0, true, false);
			assertEquals(strmsg, "", msg.getTopic());
			assertTrue(strmsg, payload.exactlyEquals(msg.getPayload()));
			assertEquals(strmsg, 0, msg.getQos());
			assertTrue(strmsg, msg.isRetained());
			assertFalse(strmsg, msg.isDuplicated());
			
			// test 2
			msg = new MqttArrivedMessage("", validData[i].array, validData[i].offset, validData[i].length, 1, false, true);
			assertEquals(strmsg, "", msg.getTopic());
			assertTrue(strmsg, payload.exactlyEquals(msg.getPayload()));
			assertEquals(strmsg, 1, msg.getQos());
			assertFalse(strmsg, msg.isRetained());
			assertTrue(strmsg, msg.isDuplicated());
			
			// test 3
			msg = new MqttArrivedMessage(TestStringData1, validData[i].array, validData[i].offset, validData[i].length, 2, true, true);
			assertEquals(strmsg, TestStringData1, msg.getTopic());
			assertTrue(strmsg, payload.exactlyEquals(msg.getPayload()));
			assertEquals(strmsg, 2, msg.getQos());
			assertTrue(strmsg, msg.isRetained());
			assertTrue(strmsg, msg.isDuplicated());
			
			// test 4
			msg = new MqttArrivedMessage(TestStringData1, validData[i].array, validData[i].offset, validData[i].length, 1, false, false);
			assertEquals(strmsg, TestStringData1, msg.getTopic());
			assertTrue(strmsg, payload.exactlyEquals(msg.getPayload()));
			assertEquals(strmsg, 1, msg.getQos());
			assertFalse(strmsg, msg.isRetained());
			assertFalse(strmsg, msg.isDuplicated());
		}
	}

	@Test
	public void testIsInvalid() {
		MqttArrivedMessage msg;
		
		// test1
		msg = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		assertTrue(msg.isInvalid());
		
		// test2
		msg = new MqttArrivedMessage(null, EmptyByteArray, 1, true, false);
		assertTrue(msg.isInvalid());
		
		// test3
		msg = new MqttArrivedMessage("", NullPayload, 2, false, true);
		assertTrue(msg.isInvalid());
		
		// test4
		msg = new MqttArrivedMessage(TestStringData1, EmptyByteArray, 1, true, true);
		assertFalse(msg.isInvalid());
		
		// test5
		msg = new MqttArrivedMessage(TestStringData1, TestStringData2.getBytes(), 1, true, true);
		assertFalse(msg.isInvalid());
	}

	@Test
	public void testIsEmpty() {
		MqttArrivedMessage msg;
		
		// test1
		msg = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		assertTrue(msg.isEmpty());
		
		// test2
		msg = new MqttArrivedMessage(null, EmptyByteArray, 1, true, false);
		assertTrue(msg.isEmpty());
		
		// test3
		msg = new MqttArrivedMessage("", NullPayload, 2, false, true);
		assertTrue(msg.isEmpty());
		
		// test4
		msg = new MqttArrivedMessage(TestStringData1, EmptyByteArray, 1, true, true);
		assertTrue(msg.isEmpty());
		
		// test5
		msg = new MqttArrivedMessage(TestStringData1, TestStringData2.getBytes(), 1, true, true);
		assertFalse(msg.isEmpty());
	}

	@Test
	public void testGetTopic() {
		MqttArrivedMessage msg;
		
		// test 1
		msg = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		assertEquals("", msg.getTopic());
		
		// test 2
		msg = new MqttArrivedMessage("", NullPayload, 0, false, false);
		assertEquals("", msg.getTopic());
		
		// test 3
		msg = new MqttArrivedMessage(TestStringData1, NullPayload, 0, false, false);
		assertEquals(TestStringData1, msg.getTopic());
		
		// test 4
		msg = new MqttArrivedMessage(TestStringData2, NullPayload, 0, false, false);
		assertEquals(TestStringData2, msg.getTopic());
	}
	
	@Test
	public void testGetPayload() {
		MqttArrivedMessage msg;
		MqPayload payload;
		
		// test 1
		msg = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		
		// test 2
		msg = new MqttArrivedMessage(null, EmptyByteArray, 0, false, false);
		assertTrue(MqPayload.EmptyPayload.exactlyEquals(msg.getPayload()));
		
		// test 3
		payload = new MqPayload(TestStringData1.getBytes());
		msg = new MqttArrivedMessage(null, TestStringData1.getBytes(), 0, false, false);
		assertTrue(payload.exactlyEquals(msg.getPayload()));
		
		// test 4
		byte[] bytedata = TestStringData2.getBytes();
		int offset = bytedata.length/2;
		int length = bytedata.length - offset;
		payload = new MqPayload(bytedata, offset, length);
		msg = new MqttArrivedMessage(null, bytedata, offset, length, 0, false, false);
		assertTrue(payload.exactlyEquals(msg.getPayload()));
	}

	@Test
	public void testGetQos() {
		MqttArrivedMessage msg;
		
		// test 1
		msg = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		assertEquals(0, msg.getQos());
		
		// test 2
		msg = new MqttArrivedMessage(null, NullPayload, 1, false, false);
		assertEquals(1, msg.getQos());
		
		// test 3
		msg = new MqttArrivedMessage(null, NullPayload, 2, false, false);
		assertEquals(2, msg.getQos());
	}

	@Test
	public void testIsRetained() {
		MqttArrivedMessage msg;
		
		// test 1
		msg = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		assertFalse(msg.isRetained());
		
		// test 2
		msg = new MqttArrivedMessage(null, NullPayload, 0, true, false);
		assertTrue(msg.isRetained());
	}

	@Test
	public void testIsDuplicated() {
		MqttArrivedMessage msg;
		
		// test 1
		msg = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		assertFalse(msg.isDuplicated());
		
		// test 2
		msg = new MqttArrivedMessage(null,  NullPayload, 0, false, true);
		assertTrue(msg.isDuplicated());
	}

	@Test
	public void testGetMessageString() {
		MqttArrivedMessage msg;
		MqPayload payload;
		
		for (int i = 0; i < validData.length; i++) {
			String strmsg = "Index(" + i + ")";
			payload = MqPayloadTest.makePayload(validData[i]);
			
			msg = new MqttArrivedMessage(null, validData[i].array, validData[i].offset, validData[i].length, 0, false, false);
			assertTrue(strmsg, payload.exactlyEquals(msg.getPayload()));
			assertEquals(strmsg, payload.toString(), msg.getMessageString());
		}
	}

	@Test
	public void testGetMessageStringString() {
		MqttArrivedMessage msg;
		MqPayload payload;
		
		for (int i = 0; i < validData.length; i++) {
			String strmsg = "Index(" + i + ")";
			payload = MqPayloadTest.makePayload(validData[i]);
			
			msg = new MqttArrivedMessage(null, validData[i].array, validData[i].offset, validData[i].length, 0, false, false);
			assertTrue(strmsg, payload.exactlyEquals(msg.getPayload()));
			
			for (String cn : CharsetNames) {
				String cnmsg = strmsg + ", \"" + String.valueOf(cn) + "\" : ";
				try {
					String aString;
					if (cn != null && cn.length() > 0)
						aString = payload.toString(cn);
					else
						aString = payload.toString();
					
					assertEquals(cnmsg, aString, msg.getMessageString(cn));
				}
				catch (Throwable ex) {
					fail("Cought exception by " + cnmsg + ex.toString());
				}
			}
		}
	}
	
	@Test
	public void testGetMessageStringSJIS() {
		MqttArrivedMessage msg;
		MqPayload payload;
		Charset cs = Charset.forName("MS932");
		
		for (int i = 0; i < validData.length; i++) {
			String strmsg = "Index(" + i + ")";
			payload = MqPayloadTest.makePayload(validData[i]);
			
			msg = new MqttArrivedMessage(null, validData[i].array, validData[i].offset, validData[i].length, 0, false, false);
			assertTrue(strmsg, payload.exactlyEquals(msg.getPayload()));
			assertEquals(strmsg, payload.toString(cs), msg.getMessageStringSJIS());
		}
	}
	
	@Test
	public void testGetMessageStringUTF8() {
		MqttArrivedMessage msg;
		MqPayload payload;
		Charset cs = Charset.forName("UTF-8");
		
		for (int i = 0; i < validData.length; i++) {
			String strmsg = "Index(" + i + ")";
			payload = MqPayloadTest.makePayload(validData[i]);
			
			msg = new MqttArrivedMessage(null, validData[i].array, validData[i].offset, validData[i].length, 0, false, false);
			assertTrue(strmsg, payload.exactlyEquals(msg.getPayload()));
			assertEquals(strmsg, payload.toString(cs), msg.getMessageStringUTF8());
		}
	}

	@Test
	public void testSaveMessageToBinaryFile() {
		MqttArrivedMessage msg;
		MqPayload payload;
		
		for (int i = 0; i < validData.length; i++) {
			String strmsg = "Index(" + i + ")";
			payload = MqPayloadTest.makePayload(validData[i]);
			msg = new MqttArrivedMessage(null, validData[i].array, validData[i].offset, validData[i].length, 0, false, false);
			
			// null exception
			try {
				msg.saveMessageToBinaryFile(null);
				fail(strmsg + " : Failed to throw NullPointerException.");
			} catch (NullPointerException ex) {
				assertTrue(true);
			}
			
			// check file
			String path = basePath + i + ".txt";
			File file = new File(path);
			byte[] aBytes = payload.toArray();
			msg.saveMessageToBinaryFile(path);
			assertTrue(strmsg, file.exists());
			assertTrue(strmsg, file.isFile());
			if (aBytes.length <= 0) {
				// empty
				assertEquals(strmsg, 0L, file.length());
			}
			else {
				byte[] readData = new byte[(int)file.length()];
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					fis.read(readData);
				}
				catch (Throwable ex) {
					fail(strmsg + " : Failed to read binary file : \"" + path + "\" : " + ex.toString());
				}
				finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (Throwable ex) {}
					}
				}
				assertTrue(strmsg, Arrays.equals(aBytes, readData));
			}
		}
	}

	@Test
	public void testSaveMessageToTextFile() {
		MqttArrivedMessage emptyMessage = new MqttArrivedMessage(null, NullPayload, 0, false, false);
		MqPayload[] ansPayloads = new MqPayload[2];
		byte[][] ansFileBytes = new byte[2][];
		for (String fileEncoding : CharsetNames) {
			String path = basePath + (fileEncoding!=null && fileEncoding.length() > 0 ? fileEncoding : "def") + ".txt";
			File file = new File(path);
			for (String msgEncoding : CharsetNames) {
				String strmsg = "msgEncoding=" + String.valueOf(msgEncoding) + ", fileEncoding=" + String.valueOf(fileEncoding) + " : ";
				
				// create test data
				try {
					if (msgEncoding != null && msgEncoding.length() > 0) {
						ansPayloads[0] = new MqPayload(TestCompareData.getBytes(msgEncoding));
						ansPayloads[1] = new MqPayload(TestCompareData.getBytes(msgEncoding),
														TestCompareData_1.getBytes(msgEncoding).length,
														TestCompareData_2.getBytes(msgEncoding).length);
					} else {
						ansPayloads[0] = new MqPayload(TestCompareData.getBytes());
						ansPayloads[1] = new MqPayload(TestCompareData.getBytes(),
														TestCompareData_1.getBytes().length,
														TestCompareData_2.getBytes().length);
					}
					if (fileEncoding != null && fileEncoding.length() > 0) {
						ansFileBytes[0] = TestCompareData.getBytes(fileEncoding);
						ansFileBytes[1] = TestCompareData_2.getBytes(fileEncoding);
					} else {
						ansFileBytes[0] = TestCompareData.getBytes();
						ansFileBytes[1] = TestCompareData_2.getBytes();
					}
				} catch (Throwable ex) {
					fail("Failed to get bytes, msgEncoding=\"" + String.valueOf(msgEncoding) + "\", fileEncoding=\"" + String.valueOf(fileEncoding) + "\" : " + ex.toString());
				}
				
				// empty
				try {
					emptyMessage.saveMessageToTextFile(null, msgEncoding, fileEncoding);
					fail(strmsg + " : NullPointerException is expected.");
				} catch (NullPointerException ex) {
					assertTrue(true);
				}
				emptyMessage.saveMessageToTextFile(path, msgEncoding, fileEncoding);
				assertTrue(strmsg, file.exists());
				assertTrue(strmsg, file.isFile());
				assertEquals(strmsg, 0L, file.length());
				
				// text data
				for (int i = 0; i < ansPayloads.length; i++) {
					String inmsg = "Index(" + i + "), " + strmsg;
					
					MqttArrivedMessage msg = new MqttArrivedMessage(TestStringData1, ansPayloads[i], 1, true, true);
					msg.saveMessageToTextFile(path, msgEncoding, fileEncoding);
					assertTrue(inmsg, file.exists());
					assertTrue(inmsg, file.isFile());
					byte[] readData = new byte[(int)file.length()];
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
						fis.read(readData);
					}
					catch (Throwable ex) {
						fail(inmsg + "Failed to read binary file : \"" + path + "\" : " + ex.toString());
					}
					finally {
						if (fis != null) {
							try {
								fis.close();
							} catch (Throwable ex) {}
						}
					}
					assertTrue(inmsg, Arrays.equals(ansFileBytes[i], readData));
				}
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
