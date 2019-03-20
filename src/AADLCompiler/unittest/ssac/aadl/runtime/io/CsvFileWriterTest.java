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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvFileWriterTest.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.io.CsvFileWriter} クラスのテスト。
 * 
 * @version 1.50	2010/09/27
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.50
 */
public class CsvFileWriterTest extends TestCase
{
	static protected File basedir = new File("testdata/unittest/runtime/csv");
	static protected File fileTest_SJIS = new File(basedir, "outputData_sjis.csv");
	static protected File fileTest_UTF8 = new File(basedir, "outputData_utf8.csv");
	static protected File fileTest2_SJIS = new File(basedir, "outoutEnquoteCheck_sjis.csv");
	static protected File notexistFile = new File("testdata/unittest/runtime/nodir/testData0.csv");

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.CsvFileWriter#CsvFileWriter(java.io.File)}.
	 */
	public void testTextFileWriterFile() {
		// null
		try {
			new CsvFileWriter(null);
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		
		// normal
		CsvFileWriter tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_SJIS);
		}
		catch (Throwable ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// file not found
		tfr = null;
		try {
			tfr = new CsvFileWriter(notexistFile);
			fail("must be throw FileNotFoundException.");
		}
		catch (FileNotFoundException ex) {
			System.out.println("Throw FileNotFoundException by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.CsvFileWriter#CsvFileWriter(java.io.File, java.lang.String)}.
	 */
	public void testTextFileWriterFileString() {
		// null
		try {
			new CsvFileWriter(null, "MS932");
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		try {
			new CsvFileWriter(fileTest_UTF8, null);
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		
		// normal
		CsvFileWriter tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_UTF8, "UTF-8");
		}
		catch (Throwable ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// file not found
		tfr = null;
		try {
			tfr = new CsvFileWriter(notexistFile, "UTF-8");
			fail("must be throw FileNotFoundException.");
		}
		catch (FileNotFoundException ex) {
			System.out.println("Throw FileNotFoundException by CsvFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// unsupported encoding
		tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_UTF8, "hoge");
			fail("must be throw UnsupportedEncodingException.");
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			System.out.println("Throw UnsupportedEncodingException by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileWriter#getFile()}.
	 */
	public void testGetFile() {
		CsvFileWriter tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_SJIS);
			assertEquals(fileTest_SJIS, tfr.getFile());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileWriter#getEncoding()}.
	 */
	public void testGetEncoding() {
		CsvFileWriter tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_SJIS);
			assertNull(tfr.getEncoding());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_UTF8, "UTF-8");
			assertEquals("UTF-8", tfr.getEncoding());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileWriter#isOpen()}.
	 */
	public void testIsOpen() {
		CsvFileWriter tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_SJIS);
			assertTrue(tfr.isOpen());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		assertFalse(tfr.isOpen());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileWriter#lastException()}.
	 */
	public void testLastException() {
		CsvFileWriter tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_SJIS);
			assertNull(tfr.lastException());
			tfr.close();
			tfr.flush();
			assertNotNull(tfr.lastException());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileWriter#close()}.
	 */
	public void testClose() {
		CsvFileWriter tfr = null;
		try {
			tfr = new CsvFileWriter(fileTest_SJIS);
			assertTrue(tfr.isOpen());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		assertFalse(tfr.isOpen());
	}

	/**
	 * Test method for writing.
	 */
	public void testWriting() {
		CsvFileWriter writer;
		CsvFileReader reader;
		
		List<List<String>> testdata = CsvFileReaderTest.testData;
		
		// sjis
		//--- write
		writer = null;
		try {
			writer = new CsvFileWriter(fileTest_SJIS);
			for (int i = 0; i < testdata.size(); i++) {
				writer.writeRecord(testdata.get(i));
				writer.flush();
				//---
				i++;
				if (i >= testdata.size())
					break;
				writer.writeFields(testdata.get(i));
				writer.newRecord();
				writer.flush();
				//---
				i++;
				if (i >= testdata.size())
					break;
				List<String> fields = testdata.get(i);
				for (int j = 0; j < fields.size(); j++) {
					writer.writeField(fields.get(j));
					//---
					j++;
					if (j >= fields.size())
						break;
					writer.newField();
					writer.writeField(fields.get(j));
					//---
					j++;
					if (j >= fields.size())
						break;
					writer.writeField(fields.get(j));
					//---
					j++;
					if (j >= fields.size())
						break;
					writer.writeFields(fields.subList(j, fields.size()));
					j = fields.size();
				}
				writer.newRecord();
			}
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (writer != null) {
				writer.close();
			}
		}
		//--- check
		reader = null;
		try {
			reader = new CsvFileReader(fileTest_SJIS);
			ArrayList<List<String>> list = new ArrayList<List<String>>();
			for (List<String> rec : reader) {
				list.add(rec);
			}
			assertEquals(testdata, list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		// utf8
		//--- write
		writer = null;
		try {
			writer = new CsvFileWriter(fileTest_UTF8, "UTF-8");
			for (int i = 0; i < testdata.size(); i++) {
				writer.writeRecord(testdata.get(i));
				writer.flush();
				//---
				i++;
				if (i >= testdata.size())
					break;
				writer.writeFields(testdata.get(i));
				writer.newRecord();
				writer.flush();
				//---
				i++;
				if (i >= testdata.size())
					break;
				List<String> fields = testdata.get(i);
				for (int j = 0; j < fields.size(); j++) {
					writer.writeField(fields.get(j));
					//---
					j++;
					if (j >= fields.size())
						break;
					writer.newField();
					writer.writeField(fields.get(j));
					//---
					j++;
					if (j >= fields.size())
						break;
					writer.writeField(fields.get(j));
					//---
					j++;
					if (j >= fields.size())
						break;
					writer.writeFields(fields.subList(j, fields.size()));
					j = fields.size();
				}
				writer.newRecord();
			}
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileWriter constructer: " + ex.toString());
		}
		finally {
			if (writer != null) {
				writer.close();
			}
		}
		//--- check
		reader = null;
		try {
			reader = new CsvFileReader(fileTest_UTF8, "UTF-8");
			ArrayList<List<String>> list = new ArrayList<List<String>>();
			for (List<String> rec : reader) {
				list.add(rec);
			}
			assertEquals(testdata, list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		// CSV enquote check
		/*
		TextFileWriter tfw = null;
		try {
			tfw = new TextFileWriter(fileTest2_SJIS);
			tfw.println("標準文字列, 標準文字列");
			tfw.println("タブ(\\t), →\t←");
			tfw.println("CR(\\r), →\t←");
			tfw.println("LF(\\n), →\n←");
			tfw.println("FormFeed(\\f), →\f←");
			tfw.println("BS(\\b), →\b←");
			tfw.println("垂直タブ(\\u000B), →\u000B←");
			tfw.println("FileSeparator(\\u001C), →\u001C←");
			tfw.println("GroupSeparator(\\u001D), →\u001D←");
			tfw.println("RecordSeparator(\\u001E), →\u001E←");
			tfw.println("UnitSeparator(\\u001F), →\u001F←");
			tfw.println("ヌル文字(\\0), →\0←");
			tfw.println("シングルクオート, →\'←");
			tfw.println("ダブルクオート, →\"←");
			tfw.println("以上, →終了←");
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfw != null) {
				tfw.close();
			}
		}
		*/
	}
}
