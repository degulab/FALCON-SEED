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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import ssac.aadl.runtime.mqtt.internal.MqttBufferedSessionImpl;

public class MqttPublisherTest
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String TestStringData2 = "テスト・ストリングデータ・２";
	
	static private final String TestTextData = "Sample Text data.\nサンプル・テキスト・データ。\n";
	
	static private final String baseInputPath  = "testdata/unittest/input/MqttPublisherTest_";

	static private final String fnameInputNone  = baseInputPath + "none.txt";
	static private final String fnameInputSJIS  = baseInputPath + "SJIS.txt";
	static private final String fnameInputMS932 = baseInputPath + "MS932.txt";
	static private final String fnameInputEUC   = baseInputPath + "EUC-JP.txt";
	static private final String fnameInputUTF8  = baseInputPath + "UTF-8.txt";
	static private final String fnameInputDef   = baseInputPath + "def.txt";
	static private final String fnameOverFile   = baseInputPath + "over.txt";
	
	static private final String[][] CharsetFiles = {
		{ null, fnameInputDef },
		{ "", fnameInputDef },
		{ "SJIS", fnameInputSJIS },
		{ "MS932", fnameInputMS932 },
		{ "UTF-8", fnameInputUTF8 },
		{ "EUC-JP", fnameInputEUC },
	};
	
	static private final String testTopic = "/mqtt/test/topic/1";

	static private final byte[] TestStringPayloadDef   = TestStringData2.getBytes();
	static private final byte[] TestStringPayloadMS932 = getStringBytes(TestStringData2, "MS932");
	static private final byte[] TestStringPayloadUTF8  = getStringBytes(TestStringData2, "UTF-8");
	static private final byte[] TestTextPayloadUTF8    = getStringBytes(TestTextData, "UTF-8");
	static private final byte[] TestTextPayloadEUC     = getStringBytes(TestTextData, "EUC-JP");
	
	static private byte[] getStringBytes(String text, String encoding) {
		try {
			return text.getBytes(encoding);
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	@BeforeClass
	static public void doBeforeClass() throws Exception
	{
		if (!new File(fnameInputSJIS).exists()) {
			makeTextFile("SJIS");
		}
		if (!new File(fnameInputMS932).exists()) {
			makeTextFile("MS932");
		}
		if (!new File(fnameInputUTF8).exists()) {
			makeTextFile("UTF-8");
		}
		if (!new File(fnameInputEUC).exists()) {
			makeTextFile("EUC-JP");
		}
		if (!new File(fnameInputDef).exists()) {
			makeTextFile(null);
		}
		
		//if (!new File(fnameBigFile).exists()) {
		//	makeBigFile(fnameBigFile, Integer.MAX_VALUE);
		//}
		
		if (!new File(fnameOverFile).exists()) {
			makeBigFile(fnameOverFile, ((long)Integer.MAX_VALUE + 1L));
		}
	}
	
	static private final void makeTextFile(String charsetName) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;

		boolean useCharset;
		File file;
		if (charsetName != null && charsetName.length() > 0) {
			useCharset = true;
			file = new File(baseInputPath + charsetName + ".txt");
		} else {
			useCharset = false;
			file = new File(fnameInputDef);
		}
		
		try {
			fos = new FileOutputStream(file);
			if (useCharset)
				osw = new OutputStreamWriter(fos, charsetName);
			else
				osw = new OutputStreamWriter(fos);
			osw.write(TestTextData);
			osw.flush();
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
		finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (Throwable ex) {}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Throwable ex) {}
			}
		}
	}
	
	static private final void makeBigFile(String filename, long filesize) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		byte[] data = new byte[4096];
		Arrays.fill(data, (byte)0);
		
		long remainSize = filesize;
		
		try {
			fos = new FileOutputStream(new File(filename));
			bos = new BufferedOutputStream(fos);
			
			for (; remainSize >= data.length; ) {
				bos.write(data);
				remainSize -= data.length;
			}
			
			if (remainSize > 0L) {
				bos.write(data, 0, (int)remainSize);
			}
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
		finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Throwable ex) {}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Throwable ex) {}
			}
		}
	}

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	@Test
	public void testGetPayloadFromBinaryFile() {
		final byte[] ansdata = TestTextData.getBytes();
		
		// normal data
		byte[] readdata = MqttPublisher.getPayloadFromBinaryFile(fnameInputDef);
		assertTrue(Arrays.equals(readdata, ansdata));
		
		// file not found
		assertFalse(new File(fnameInputNone).exists());
		try {
			MqttPublisher.getPayloadFromBinaryFile(fnameInputNone);
			fail("MqttRuntimeException is expected.");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(ex.getCause() instanceof FileNotFoundException);
		}
		
		// over size
		assertTrue(new File(fnameOverFile).exists());
		try {
			MqttPublisher.getPayloadFromBinaryFile(fnameOverFile);
			fail("IllegalArgumentException is expected.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetPayloadFromTextFile() throws Exception
	{
		byte[] ansdata;
		byte[] readdata;
		
		// normal
		for (String[] csNameAndFile : CharsetFiles) {
			String fileEncoding = csNameAndFile[0];
			String filename = csNameAndFile[1];
			File file = new File(filename);
			assertTrue(file.exists());
			for (String[] csMsgName : CharsetFiles) {
				String msgEncoding = csMsgName[0];
				String testmsg = String.format("filename:%s, fileEncoding=%s, msgEncoding=%s",
						String.valueOf(file.getName()), String.valueOf(fileEncoding), String.valueOf(msgEncoding));
				if (msgEncoding != null && msgEncoding.length() > 0)
					ansdata = TestTextData.getBytes(msgEncoding);
				else
					ansdata = TestTextData.getBytes();
				readdata = MqttPublisher.getPayloadFromTextFile(filename, msgEncoding, fileEncoding);
				assertTrue(testmsg, Arrays.equals(readdata, ansdata));
			}
		}
		
		// file not found
		assertFalse(new File(fnameInputNone).exists());
		try {
			MqttPublisher.getPayloadFromTextFile(fnameInputNone, null, null);
			fail("MqttRuntimeException is expected.");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(ex.getCause() instanceof FileNotFoundException);
		}
		try {
			MqttPublisher.getPayloadFromTextFile(fnameInputNone, "", "UTF-8");
			fail("MqttRuntimeException is expected.");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(ex.getCause() instanceof FileNotFoundException);
		}
		try {
			MqttPublisher.getPayloadFromTextFile(fnameInputNone, "UTF-8", null);
			fail("MqttRuntimeException is expected.");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(ex.getCause() instanceof FileNotFoundException);
		}
		
		// over size
		assertTrue(new File(fnameOverFile).exists());
		try {
			MqttPublisher.getPayloadFromTextFile(fnameOverFile, null, null);
			fail("IllegalArgumentException is expected.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			MqttPublisher.getPayloadFromTextFile(fnameOverFile, "", "UTF-8");
			fail("IllegalArgumentException is expected.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			MqttPublisher.getPayloadFromTextFile(fnameOverFile, "UTF-8", null);
			fail("IllegalArgumentException is expected.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
	}

	@Test
	public void testPublishStringMqttSessionStringString() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishString(null, testTopic, TestStringData2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishString(session, null, TestStringData2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishString(session, testTopic, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishString(session, testTopic, TestStringData2);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestStringPayloadDef, session.inPayload));
		assertEquals(1, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishStringMqttSessionStringStringIntBoolean() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishString(null, testTopic, TestStringData2, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishString(session, null, TestStringData2, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishString(session, testTopic, null, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishString(session, testTopic, TestStringData2, 2, false);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestStringPayloadDef, session.inPayload));
		assertEquals(2, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishStringSJISMqttSessionStringString() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishStringSJIS(null, testTopic, TestStringData2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishStringSJIS(session, null, TestStringData2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishStringSJIS(session, testTopic, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishStringSJIS(session, testTopic, TestStringData2);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestStringPayloadMS932, session.inPayload));
		assertEquals(1, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishStringSJISMqttSessionStringStringIntBoolean() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishStringSJIS(null, testTopic, TestStringData2, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishStringSJIS(session, null, TestStringData2, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishStringSJIS(session, testTopic, null, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishStringSJIS(session, testTopic, TestStringData2, 2, false);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestStringPayloadMS932, session.inPayload));
		assertEquals(2, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishStringUTF8MqttSessionStringString() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishStringUTF8(null, testTopic, TestStringData2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishStringUTF8(session, null, TestStringData2);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishStringUTF8(session, testTopic, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishStringUTF8(session, testTopic, TestStringData2);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestStringPayloadUTF8, session.inPayload));
		assertEquals(1, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishStringUTF8MqttSessionStringStringIntBoolean() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishStringUTF8(null, testTopic, TestStringData2, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishStringUTF8(session, null, TestStringData2, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishStringUTF8(session, testTopic, null, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishStringUTF8(session, testTopic, TestStringData2, 2, false);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestStringPayloadUTF8, session.inPayload));
		assertEquals(2, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishStringMqttSessionStringStringString() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishString(null, testTopic, TestStringData2, "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishString(session, null, TestStringData2, "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishString(session, testTopic, null, "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishString(session, testTopic, TestStringData2, "UTF-8");
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestStringPayloadUTF8, session.inPayload));
		assertEquals(1, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishStringMqttSessionStringStringStringIntBoolean() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishString(null, testTopic, TestStringData2, "UTF-8", 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishString(session, null, TestStringData2, "UTF-8", 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishString(session, testTopic, null, "UTF-8", 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishString(session, testTopic, TestStringData2, "UTF-8", 2, false);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestStringPayloadUTF8, session.inPayload));
		assertEquals(2, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishBinaryFileMqttSessionStringString() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishBinaryFile(null, testTopic, fnameInputUTF8);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishBinaryFile(session, null, fnameInputUTF8);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishBinaryFile(session, testTopic, null);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// file not found
		assertFalse(new File(fnameInputNone).exists());
		try {
			MqttPublisher.publishBinaryFile(session, testTopic, fnameInputNone);
			fail("MqttRuntimeException is expected.");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(ex.getCause() instanceof FileNotFoundException);
		}
		
		// over size
		assertTrue(new File(fnameOverFile).exists());
		try {
			MqttPublisher.publishBinaryFile(session, testTopic, fnameOverFile);
			fail("IllegalArgumentException is expected.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishBinaryFile(session, testTopic, fnameInputUTF8);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestTextPayloadUTF8, session.inPayload));
		assertEquals(1, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishBinaryFileMqttSessionStringStringIntBoolean() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishBinaryFile(null, testTopic, fnameInputUTF8, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishBinaryFile(session, null, fnameInputUTF8, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishBinaryFile(session, testTopic, null, 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// file not found
		assertFalse(new File(fnameInputNone).exists());
		try {
			MqttPublisher.publishBinaryFile(session, testTopic, fnameInputNone, 0, false);
			fail("MqttRuntimeException is expected.");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(ex.getCause() instanceof FileNotFoundException);
		}
		
		// over size
		assertTrue(new File(fnameOverFile).exists());
		try {
			MqttPublisher.publishBinaryFile(session, testTopic, fnameOverFile, 0, false);
			fail("IllegalArgumentException is expected.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishBinaryFile(session, testTopic, fnameInputUTF8, 2, false);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestTextPayloadUTF8, session.inPayload));
		assertEquals(2, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishTextFileMqttSessionStringStringStringString() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishTextFile(null, testTopic, fnameInputUTF8, "EUC-JP", "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishTextFile(session, null, fnameInputUTF8, "EUC-JP", "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishTextFile(session, testTopic, null, "EUC-JP", "UTF-8");
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// file not found
		assertFalse(new File(fnameInputNone).exists());
		try {
			MqttPublisher.publishTextFile(session, testTopic, fnameInputNone, "EUC-JP", "UTF-8");
			fail("MqttRuntimeException is expected.");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(ex.getCause() instanceof FileNotFoundException);
		}
		
		// over size
		assertTrue(new File(fnameOverFile).exists());
		try {
			MqttPublisher.publishTextFile(session, testTopic, fnameOverFile, "EUC-JP", "UTF-8");
			fail("IllegalArgumentException is expected.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishTextFile(session, testTopic, fnameInputUTF8, "EUC-JP", "UTF-8");
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestTextPayloadEUC, session.inPayload));
		assertEquals(1, session.inQos);
		assertFalse(session.inRetained);
	}

	@Test
	public void testPublishTextFileMqttSessionStringStringStringStringIntBoolean() {
		PublishTestSession session = new PublishTestSession();
		
		// check
		try {
			MqttPublisher.publishTextFile(null, testTopic, fnameInputUTF8, "EUC-JP", "UTF-8", 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishTextFile(session, null, fnameInputUTF8, "EUC-JP", "UTF-8", 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		try {
			MqttPublisher.publishTextFile(session, testTopic, null, "EUC-JP", "UTF-8", 0, false);
			fail("NullPointerException is expected.");
		} catch (NullPointerException ex) {}
		
		// file not found
		assertFalse(new File(fnameInputNone).exists());
		try {
			MqttPublisher.publishTextFile(session, testTopic, fnameInputNone, "EUC-JP", "UTF-8", 0, false);
			fail("MqttRuntimeException is expected.");
		}
		catch (MqttRuntimeException ex) {
			assertTrue(ex.getCause() instanceof FileNotFoundException);
		}
		
		// over size
		assertTrue(new File(fnameOverFile).exists());
		try {
			MqttPublisher.publishTextFile(session, testTopic, fnameOverFile, "EUC-JP", "UTF-8", 0, false);
			fail("IllegalArgumentException is expected.");
		}
		catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		// call check
		session.clear();
		assertNull(session.inTopic);
		assertNull(session.inPayload);
		assertEquals(-1, session.inQos);
		assertEquals(true, session.inRetained);
		MqttPublisher.publishTextFile(session, testTopic, fnameInputUTF8, "EUC-JP", "UTF-8", 2, false);
		assertEquals(session.inTopic, testTopic);
		assertTrue(Arrays.equals(TestTextPayloadEUC, session.inPayload));
		assertEquals(2, session.inQos);
		assertFalse(session.inRetained);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static class PublishTestSession extends MqttBufferedSessionImpl
	{
		public String	inTopic;
		public byte[]	inPayload;
		public int		inQos;
		public boolean	inRetained;
		
		public PublishTestSession() {
			super(new MqttConnectionParams());
			clear();
		}
		
		public void clear() {
			inTopic    = null;
			inPayload  = null;
			inQos      = -1;
			inRetained = true;
		}

		@Override
		public MqDeliveryToken asyncPublish(String topic, byte[] message, int qos, boolean retained) {
			inTopic    = topic;
			inPayload  = message;
			inQos      = qos;
			inRetained = retained;
			return null;
		}

		@Override
		public void publishAndWait(String topic, byte[] message, int qos, boolean retained) {
			inTopic    = topic;
			inPayload  = message;
			inQos      = qos;
			inRetained = retained;
		}
	}
}
