package ssac.aadl.runtime.mqtt;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Test;

public class MqPayloadTest
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static private final String TestStringData1 = "Test string data 1.";
	static private final String TestTextData = "Sample Text data.\nサンプル・テキスト・データ。\n";
	
	static private final String[] CharsetNames = {
		"SJIS", "MS932", "UTF-8", "EUC-JP",
	};
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static public class TestData {
		public byte[]	array;
		public int		offset;
		public int		length;
	};
	
	static public TestData makeTestData(byte[] inArray, int inOffset, int inLength) {
		TestData data = new TestData();
		data.array  = inArray;
		data.offset = inOffset;
		data.length = inLength;
		return data;
	}
	
	static public MqPayload makePayload(TestData data) {
		return new MqPayload(data.array, data.offset, data.length);
	}
	
	static public boolean equalsPayload(MqPayload data1, MqPayload data2) {
		if (data1 == data2)
			return true;
		
		if (data1 == null)
			return false;
		if (data2 == null)
			return false;
		
		if (Arrays.equals(data1.getData(), data2.getData()) && data1.getOffset()==data2.getOffset() && data1.getLength()==data2.getLength()) {
			return true;
		} else {
			return false;
		}
	}
	
	static protected byte[] toArray(TestData data) {
		return Arrays.copyOfRange(data.array, data.offset, data.offset+data.length);
	}
	
	static protected int hashCode(TestData data) {
		return Arrays.hashCode(toArray(data));
	}
	
	static protected int exactlyHashCode(TestData data) {
		int h = Arrays.hashCode(data.array);
		h = 31 * h + data.offset;
		h = 31 * h + data.length;
		return h;
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	@Test
	public void testMqPayloadByteArray() {
		MqPayload payload;
		
		// null
		payload = new MqPayload(null);
		assertEquals(0, payload.getData().length);
		assertEquals(0, payload.getOffset());
		assertEquals(0, payload.getLength());
		
		// empty
		payload = new MqPayload(new byte[0]);
		assertEquals(0, payload.getData().length);
		assertEquals(0, payload.getOffset());
		assertEquals(0, payload.getLength());
		
		// data
		payload = new MqPayload(TestStringData1.getBytes());
		assertTrue(Arrays.equals(payload.getData(), TestStringData1.getBytes()));
		assertEquals(payload.getOffset(), 0);
		assertEquals(payload.getLength(), TestStringData1.getBytes().length);
	}

	@Test
	public void testMqPayloadByteArrayInt() {
		final byte[] byteData = TestStringData1.getBytes();
		final TestData[] validData = {
				makeTestData(null, 0, 0),
				makeTestData(new byte[0], 0, 0),
				makeTestData(byteData, 0, byteData.length),
				makeTestData(byteData, byteData.length/2, byteData.length-byteData.length/2),
				makeTestData(byteData, byteData.length, 0),
		};
		
		final TestData[] invalidData = {
				makeTestData(null, -1, 0),
				makeTestData(null, 10, 0),
				makeTestData(new byte[0], -1, 0),
				makeTestData(new byte[0], 10, 0),
				makeTestData(byteData, -1, 0),
				makeTestData(byteData, byteData.length+1, 0),
		};
		
		MqPayload payload;
		
		// check invalid
		for (int i = 0; i < invalidData.length; i++) {
			try {
				payload = new MqPayload(invalidData[i].array, invalidData[i].offset);
				fail("index(" + i + ") IndexOutOfBoundsException is expected.");
			}
			catch (IndexOutOfBoundsException ex) {
				assertTrue(true);
			}
		}
		
		// valid
		for (int i = 0; i < validData.length; i++) {
			payload = new MqPayload(validData[i].array, validData[i].offset);
			String msg = "index(" + i + ")";
			if (validData[i].array == null) {
				assertEquals(msg, payload.getData().length, 0);
			} else {
				assertTrue(msg, Arrays.equals(payload.getData(), validData[i].array));
			}
			assertEquals(msg, payload.getOffset(), validData[i].offset);
			assertEquals(msg, payload.getLength(), validData[i].length);
		}
	}

	@Test
	public void testMqPayloadByteArrayIntInt() {
		final byte[] byteData = TestStringData1.getBytes();
		final TestData[] validData = {
				makeTestData(null, 0, 0),
				makeTestData(new byte[0], 0, 0),
				makeTestData(byteData, 0, 0),
				makeTestData(byteData, 0, byteData.length/2),
				makeTestData(byteData, 0, byteData.length),
				makeTestData(byteData, byteData.length/2, 0),
				makeTestData(byteData, byteData.length/2, (byteData.length-byteData.length/2)/2),
				makeTestData(byteData, byteData.length/2, byteData.length-byteData.length/2),
				makeTestData(byteData, byteData.length, 0),
		};
		
		final TestData[] invalidData = {
				makeTestData(null, -1, 0),
				makeTestData(null, -1, -1),
				makeTestData(null, -1, 1),
				makeTestData(null, 10, 0),
				makeTestData(null, 10, -1),
				makeTestData(null, 10, 10),
				makeTestData(new byte[0], -1, 0),
				makeTestData(new byte[0], -1, -1),
				makeTestData(new byte[0], -1, 10),
				makeTestData(new byte[0], 10, 0),
				makeTestData(new byte[0], 10, -1),
				makeTestData(new byte[0], 10, 10),
				makeTestData(byteData, -1, 0),
				makeTestData(byteData, -1, -1),
				makeTestData(byteData, -1, 10),
				makeTestData(byteData, 0, -1),
				makeTestData(byteData, 0, byteData.length+1),
				makeTestData(byteData, byteData.length, -1),
				makeTestData(byteData, byteData.length, 1),
				makeTestData(byteData, byteData.length+1, 0),
				makeTestData(byteData, byteData.length+1, -1),
				makeTestData(byteData, byteData.length+1, 1),
		};
		
		MqPayload payload;
		
		// check invalid
		for (int i = 0; i < invalidData.length; i++) {
			try {
				payload = new MqPayload(invalidData[i].array, invalidData[i].offset, invalidData[i].length);
				fail("index(" + i + ") IndexOutOfBoundsException is expected.");
			}
			catch (IndexOutOfBoundsException ex) {
				assertTrue(true);
			}
		}
		
		// valid
		for (int i = 0; i < validData.length; i++) {
			payload = new MqPayload(validData[i].array, validData[i].offset, validData[i].length);
			String msg = "index(" + i + ")";
			if (validData[i].array == null) {
				assertEquals(msg, payload.getData().length, 0);
			} else {
				assertTrue(msg, Arrays.equals(payload.getData(), validData[i].array));
			}
			assertEquals(msg, payload.getOffset(), validData[i].offset);
			assertEquals(msg, payload.getLength(), validData[i].length);
		}
	}

	@Test
	public void testIsEmpty() {
		MqPayload payload;
		
		// null
		payload = new MqPayload(null);
		assertEquals(0, payload.getData().length);
		assertEquals(0, payload.getOffset());
		assertEquals(0, payload.getLength());
		assertTrue(payload.isEmpty());
		
		// empty
		payload = new MqPayload(new byte[0]);
		assertEquals(0, payload.getData().length);
		assertEquals(0, payload.getOffset());
		assertEquals(0, payload.getLength());
		assertTrue(payload.isEmpty());
		
		// data
		byte[] byteData = TestStringData1.getBytes();
		payload = new MqPayload(byteData);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), 0);
		assertEquals(payload.getLength(), byteData.length);
		assertFalse(payload.isEmpty());
		
		payload = new MqPayload(byteData, byteData.length/2, 1);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length/2);
		assertEquals(payload.getLength(), 1);
		assertFalse(payload.isEmpty());
		
		payload = new MqPayload(byteData, byteData.length/2, 0);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length/2);
		assertEquals(payload.getLength(), 0);
		assertTrue(payload.isEmpty());
		
		payload = new MqPayload(byteData, byteData.length, 0);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length);
		assertEquals(payload.getLength(), 0);
		assertTrue(payload.isEmpty());
	}

	@Test
	public void testToArray() {
		MqPayload payload;
		byte[] result;
		
		// null
		payload = new MqPayload(null);
		assertEquals(0, payload.getData().length);
		assertEquals(0, payload.getOffset());
		assertEquals(0, payload.getLength());
		result = payload.toArray();
		assertEquals(0, result.length);
		
		// empty
		payload = new MqPayload(new byte[0]);
		assertEquals(0, payload.getData().length);
		assertEquals(0, payload.getOffset());
		assertEquals(0, payload.getLength());
		result = payload.toArray();
		assertEquals(0, result.length);
		
		// data
		byte[] byteData = TestStringData1.getBytes();
		payload = new MqPayload(byteData);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), 0);
		assertEquals(payload.getLength(), byteData.length);
		result = payload.toArray();
		assertTrue(Arrays.equals(result, byteData));
		
		payload = new MqPayload(byteData, byteData.length/2, 1);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length/2);
		assertEquals(payload.getLength(), 1);
		result = payload.toArray();
		assertTrue(Arrays.equals(result, Arrays.copyOfRange(byteData, payload.getOffset(), payload.getOffset()+payload.getLength())));
		
		payload = new MqPayload(byteData, byteData.length/2, 0);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length/2);
		assertEquals(payload.getLength(), 0);
		result = payload.toArray();
		assertEquals(0, result.length);
		
		payload = new MqPayload(byteData, byteData.length, 0);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length);
		assertEquals(payload.getLength(), 0);
		result = payload.toArray();
		assertEquals(0, result.length);
	}
	
	@Test
	public void testEqualsAndHashCode() {
		final byte[] bdata0 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		final byte[] bdata1 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		final byte[] bdata2 = {1,2,3,4,5,0,0,0,0,0,0,0,0,0,0};
		final byte[] bdata3 = {0,0,0,0,0,1,2,3,4,5,0,0,0,0,0};
		final byte[] bdata4 = {0,0,0,0,0,0,0,0,0,0,1,2,3,4,5};
		final TestData[] validdata = {
				makeTestData(bdata0, 0, bdata0.length),
				makeTestData(bdata1, bdata0.length, 0),
				makeTestData(bdata2, 0, 5),
				makeTestData(bdata3, 5, 5),
				makeTestData(bdata4, 10, 5),
		};
		
		MqPayload[] payloads = new MqPayload[validdata.length];
		for (int i = 0; i < validdata.length; i++) {
			payloads[i] = new MqPayload(validdata[i].array, validdata[i].offset, validdata[i].length);
			// check hashcode
			// check hashcode
			int hr = payloads[i].hashCode();
			int ha = hashCode(validdata[i]);
			assertEquals("Index(" + i + ")", hr, ha);
			assertFalse("Index(" + i + ")", payloads[i].equals(null));
			assertFalse("Index(" + i + ")", payloads[i].equals(new Object()));
		}
		
		// check hashcode
		assertFalse(payloads[0].hashCode() == payloads[1].hashCode());
		assertFalse(payloads[0].hashCode() == payloads[2].hashCode());
		assertFalse(payloads[0].hashCode() == payloads[3].hashCode());
		assertFalse(payloads[0].hashCode() == payloads[4].hashCode());
		assertFalse(payloads[1].hashCode() == payloads[2].hashCode());
		assertFalse(payloads[1].hashCode() == payloads[3].hashCode());
		assertFalse(payloads[1].hashCode() == payloads[4].hashCode());
		assertTrue(payloads[2].hashCode() == payloads[3].hashCode());
		assertTrue(payloads[2].hashCode() == payloads[4].hashCode());
		assertTrue(payloads[3].hashCode() == payloads[4].hashCode());
		
		// check equals
		assertTrue(payloads[0].equals(payloads[0]));
		assertTrue(payloads[0].equals(makePayload(validdata[0])));
		assertFalse(payloads[0].equals(payloads[1]));
		assertFalse(payloads[0].equals(payloads[2]));
		assertFalse(payloads[0].equals(payloads[3]));
		assertFalse(payloads[0].equals(payloads[4]));
		assertFalse(payloads[1].equals(payloads[2]));
		assertFalse(payloads[1].equals(payloads[3]));
		assertFalse(payloads[1].equals(payloads[4]));
		assertTrue(payloads[2].equals(payloads[3]));
		assertTrue(payloads[2].equals(payloads[4]));
		assertTrue(payloads[3].equals(payloads[4]));
	}
	
	@Test
	public void testExactlyEqualsAndHashCode() {
		final byte[] bdata0 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		final byte[] bdata1 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		final byte[] bdata2 = {1,2,3,4,5,0,0,0,0,0,0,0,0,0,0};
		final byte[] bdata3 = {0,0,0,0,0,1,2,3,4,5,0,0,0,0,0};
		final byte[] bdata4 = {0,0,0,0,0,0,0,0,0,0,1,2,3,4,5};
		final TestData[] validdata = {
				makeTestData(bdata0, 0, bdata0.length),
				makeTestData(bdata1, bdata0.length, 0),
				makeTestData(bdata2, 0, 5),
				makeTestData(bdata3, 5, 5),
				makeTestData(bdata4, 10, 5),
		};
		
		MqPayload[] payloads = new MqPayload[validdata.length];
		for (int i = 0; i < validdata.length; i++) {
			payloads[i] = new MqPayload(validdata[i].array, validdata[i].offset, validdata[i].length);
			// check hashcode
			// check hashcode
			int hr = payloads[i].exactlyHashCode();
			int ha = exactlyHashCode(validdata[i]);
			assertEquals("Index(" + i + ")", hr, ha);
			assertFalse("Index(" + i + ")", payloads[i].exactlyEquals(null));
			assertFalse("Index(" + i + ")", payloads[i].exactlyEquals(new Object()));
		}
		
		// check hashcode
		assertFalse(payloads[0].exactlyHashCode() == payloads[1].exactlyHashCode());
		assertFalse(payloads[0].exactlyHashCode() == payloads[2].exactlyHashCode());
		assertFalse(payloads[0].exactlyHashCode() == payloads[3].exactlyHashCode());
		assertFalse(payloads[0].exactlyHashCode() == payloads[4].exactlyHashCode());
		assertFalse(payloads[1].exactlyHashCode() == payloads[2].exactlyHashCode());
		assertFalse(payloads[1].exactlyHashCode() == payloads[3].exactlyHashCode());
		assertFalse(payloads[1].exactlyHashCode() == payloads[4].exactlyHashCode());
		assertFalse(payloads[2].exactlyHashCode() == payloads[3].exactlyHashCode());
		assertFalse(payloads[2].exactlyHashCode() == payloads[4].exactlyHashCode());
		assertFalse(payloads[3].exactlyHashCode() == payloads[4].exactlyHashCode());
		
		// check equals
		assertTrue(payloads[0].exactlyEquals(payloads[0]));
		assertTrue(payloads[0].exactlyEquals(makePayload(validdata[0])));
		assertTrue(payloads[3].exactlyEquals(payloads[3]));
		assertTrue(payloads[3].exactlyEquals(makePayload(validdata[3])));
		assertFalse(payloads[0].exactlyEquals(payloads[1]));
		assertFalse(payloads[0].exactlyEquals(payloads[2]));
		assertFalse(payloads[0].exactlyEquals(payloads[3]));
		assertFalse(payloads[0].exactlyEquals(payloads[4]));
		assertFalse(payloads[1].exactlyEquals(payloads[2]));
		assertFalse(payloads[1].exactlyEquals(payloads[3]));
		assertFalse(payloads[1].exactlyEquals(payloads[4]));
		assertFalse(payloads[2].exactlyEquals(payloads[3]));
		assertFalse(payloads[2].exactlyEquals(payloads[4]));
		assertFalse(payloads[3].exactlyEquals(payloads[4]));
	}

	@Test
	public void testToString() {
		MqPayload payload;
		String result;
		
		// null
		payload = new MqPayload(null);
		assertEquals(0, payload.getData().length);
		assertEquals(0, payload.getOffset());
		assertEquals(0, payload.getLength());
		result = payload.toString();
		assertEquals(result, "");
		
		// empty
		payload = new MqPayload(new byte[0]);
		assertEquals(0, payload.getData().length);
		assertEquals(0, payload.getOffset());
		assertEquals(0, payload.getLength());
		result = payload.toString();
		assertEquals(result, "");
		
		// data
		byte[] byteData = TestTextData.getBytes();
		payload = new MqPayload(byteData);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), 0);
		assertEquals(payload.getLength(), byteData.length);
		result = payload.toString();
		assertEquals(result, new String(byteData));
		
		payload = new MqPayload(byteData, byteData.length/2, 1);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length/2);
		assertEquals(payload.getLength(), 1);
		result = payload.toString();
		assertEquals(result, new String(Arrays.copyOfRange(byteData, payload.getOffset(), payload.getOffset()+payload.getLength())));
		
		payload = new MqPayload(byteData, byteData.length/2, 0);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length/2);
		assertEquals(payload.getLength(), 0);
		result = payload.toString();
		assertEquals(result, "");
		
		payload = new MqPayload(byteData, byteData.length, 0);
		assertTrue(Arrays.equals(payload.getData(), byteData));
		assertEquals(payload.getOffset(), byteData.length);
		assertEquals(payload.getLength(), 0);
		result = payload.toString();
		assertEquals(result, "");
	}

	@Test
	public void testToStringCharset() {
		MqPayload payload;
		String result;
		byte[] byteData = TestTextData.getBytes();
		
		// NullPointerException
		payload = new MqPayload(byteData);
		try {
			payload.toString((Charset)null);
			fail("NullPointerException is expected.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check valid data
		for (int i = 0; i < CharsetNames.length; i++) {
			Charset cs = Charset.forName(CharsetNames[i]);
			String msg = "Index(" + i + ")";
			
			// null
			payload = new MqPayload(null);
			assertEquals(msg, 0, payload.getData().length);
			assertEquals(msg, 0, payload.getOffset());
			assertEquals(msg, 0, payload.getLength());
			result = payload.toString(cs);
			assertEquals(msg, result, "");
			
			// empty
			payload = new MqPayload(new byte[0]);
			assertEquals(msg, 0, payload.getData().length);
			assertEquals(msg, 0, payload.getOffset());
			assertEquals(msg, 0, payload.getLength());
			result = payload.toString(cs);
			assertEquals(msg, result, "");
			
			// data
			payload = new MqPayload(byteData);
			assertTrue(msg, Arrays.equals(payload.getData(), byteData));
			assertEquals(msg, payload.getOffset(), 0);
			assertEquals(msg, payload.getLength(), byteData.length);
			result = payload.toString(cs);
			assertEquals(msg, result, new String(byteData, cs));
			
			payload = new MqPayload(byteData, byteData.length/2, 1);
			assertTrue(msg, Arrays.equals(payload.getData(), byteData));
			assertEquals(msg, payload.getOffset(), byteData.length/2);
			assertEquals(msg, payload.getLength(), 1);
			result = payload.toString(cs);
			assertEquals(msg, result, new String(Arrays.copyOfRange(byteData, payload.getOffset(), payload.getOffset()+payload.getLength()), cs));
			
			payload = new MqPayload(byteData, byteData.length/2, 0);
			assertTrue(msg, Arrays.equals(payload.getData(), byteData));
			assertEquals(msg, payload.getOffset(), byteData.length/2);
			assertEquals(msg, payload.getLength(), 0);
			result = payload.toString(cs);
			assertEquals(msg, result, "");
			
			payload = new MqPayload(byteData, byteData.length, 0);
			assertTrue(msg, Arrays.equals(payload.getData(), byteData));
			assertEquals(msg, payload.getOffset(), byteData.length);
			assertEquals(msg, payload.getLength(), 0);
			result = payload.toString(cs);
			assertEquals(msg, result, "");
		}
	}

	@Test
	public void testToStringString() {
		MqPayload payload;
		String result;
		byte[] byteData = TestTextData.getBytes();
		
		// NullPointerException
		payload = new MqPayload(byteData);
		try {
			payload.toString((String)null);
			fail("NullPointerException is expected.");
		}
		catch (UnsupportedEncodingException ex) {
			fail("NullPointerException is expected.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// UnsupportedEncodingException
		try {
			payload.toString("");
			fail("UnsupportedEncodingException is expected.");
		}
		catch (UnsupportedEncodingException ex) {
			assertTrue(true);
		}
		try {
			payload.toString("hoge");
			fail("UnsupportedEncodingException is expected.");
		}
		catch (UnsupportedEncodingException ex) {
			assertTrue(true);
		}
		
		// check valid data
		for (int i = 0; i < CharsetNames.length; i++) {
			String encoding = CharsetNames[i];
			String msg = "Index(" + i + ")";

			try {
				// null
				payload = new MqPayload(null);
				assertEquals(msg, 0, payload.getData().length);
				assertEquals(msg, 0, payload.getOffset());
				assertEquals(msg, 0, payload.getLength());
				result = payload.toString(encoding);
				assertEquals(msg, result, "");
				
				// empty
				payload = new MqPayload(new byte[0]);
				assertEquals(msg, 0, payload.getData().length);
				assertEquals(msg, 0, payload.getOffset());
				assertEquals(msg, 0, payload.getLength());
				result = payload.toString(encoding);
				assertEquals(msg, result, "");
				
				// data
				payload = new MqPayload(byteData);
				assertTrue(msg, Arrays.equals(payload.getData(), byteData));
				assertEquals(msg, payload.getOffset(), 0);
				assertEquals(msg, payload.getLength(), byteData.length);
				result = payload.toString(encoding);
				assertEquals(msg, result, new String(byteData, encoding));
				
				payload = new MqPayload(byteData, byteData.length/2, 1);
				assertTrue(msg, Arrays.equals(payload.getData(), byteData));
				assertEquals(msg, payload.getOffset(), byteData.length/2);
				assertEquals(msg, payload.getLength(), 1);
				result = payload.toString(encoding);
				assertEquals(msg, result, new String(Arrays.copyOfRange(byteData, payload.getOffset(), payload.getOffset()+payload.getLength()), encoding));
				
				payload = new MqPayload(byteData, byteData.length/2, 0);
				assertTrue(msg, Arrays.equals(payload.getData(), byteData));
				assertEquals(msg, payload.getOffset(), byteData.length/2);
				assertEquals(msg, payload.getLength(), 0);
				result = payload.toString(encoding);
				assertEquals(msg, result, "");
				
				payload = new MqPayload(byteData, byteData.length, 0);
				assertTrue(msg, Arrays.equals(payload.getData(), byteData));
				assertEquals(msg, payload.getOffset(), byteData.length);
				assertEquals(msg, payload.getLength(), 0);
				result = payload.toString(encoding);
				assertEquals(msg, result, "");
			}
			catch (UnsupportedEncodingException ex) {
				fail("Index(" + i + ") : Caught UnsupportedEncodingException : " + ex);
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
