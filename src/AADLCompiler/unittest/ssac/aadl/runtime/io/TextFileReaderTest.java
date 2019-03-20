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
 * @(#)TextFileReaderTest.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.io.TextFileReader} クラスのテスト。
 * 
 * @version 1.50	2010/09/27
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.50
 */
public class TextFileReaderTest extends TestCase
{
	static protected File basedir = new File("testdata/unittest/runtime/text");
	static protected File fileTest1a_SJIS = new File(basedir, "testData1a_sjis.txt");
	static protected File fileTest1a_UTF8 = new File(basedir, "testData1a_utf8.txt");
	static protected File fileTest2a_SJIS = new File(basedir, "testData2a_sjis.txt");
	static protected File fileTest2a_UTF8 = new File(basedir, "testData2a_utf8.txt");
	static protected File fileTest1b_SJIS = new File(basedir, "testData1b_sjis.txt");
	static protected File fileTest1b_UTF8 = new File(basedir, "testData1b_utf8.txt");
	static protected File fileTest2b_SJIS = new File(basedir, "testData2b_sjis.txt");
	static protected File fileTest2b_UTF8 = new File(basedir, "testData2b_utf8.txt");
	static protected File notexistFile = new File(basedir, "testData0.txt");
	
	static protected String[] testData1 = {
		"1.About AADL",
		"",
		"Algebraic Accounting Description Language (AADL) is the specification description language that has expressions very near to exchange algebra.",
		"The description of AADL is transferred into a JAVA program by the specialized pre-processor (AADL compiler),"
		+ "so that the program is converted into an executable file format (JAR) using the JAVA platform.",
		"For this reason, the AADL compiler itself is also implemented by JAVA.",
		"In this Description Guide, the description methods used by AADL are described and how to use the AADL compiler is explained.",
	};
	
	static protected String[] testData2 = {
		"1. AADL について",
		"",
		"AADL（Algebraic Accounting Description Language）は、交換代数に極めて近い表現を持つ使用記述言語です。",
		"AADL の記述は、専用のプリプロセッサー（AADL コンパイラー）によりJAVA プログラムに転換され、"
		+ "JAVA プラットフォーム上で実行可能な形式（JAR）に転換されます。",
		"そのため、AADL コンパイラー自身もJAVA で実装されています。",
		"本書では、AADL の記述方法、ならびにAADL コンパイラーの利用方法について説明します。",
	};
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.TextFileReader#TextFileReader(java.io.File)}.
	 */
	public void testTextFileReaderFile() {
		// null
		try {
			new TextFileReader(null);
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		
		// normal
		TextFileReader tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_SJIS);
		}
		catch (Throwable ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// file not found
		tfr = null;
		try {
			tfr = new TextFileReader(notexistFile);
			fail("must be throw FileNotFoundException.");
		}
		catch (FileNotFoundException ex) {
			System.out.println("Throw FileNotFoundException by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.TextFileReader#TextFileReader(java.io.File, java.lang.String)}.
	 */
	public void testTextFileReaderFileString() {
		// null
		try {
			new TextFileReader(null, "MS932");
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		try {
			new TextFileReader(fileTest1a_UTF8, null);
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		
		// normal
		TextFileReader tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_UTF8, "UTF-8");
		}
		catch (Throwable ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// file not found
		tfr = null;
		try {
			tfr = new TextFileReader(notexistFile, "UTF-8");
			fail("must be throw FileNotFoundException.");
		}
		catch (FileNotFoundException ex) {
			System.out.println("Throw FileNotFoundException by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// unsupported encoding
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_UTF8, "hoge");
			fail("must be throw UnsupportedEncodingException.");
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			System.out.println("Throw UnsupportedEncodingException by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#getFile()}.
	 */
	public void testGetFile() {
		TextFileReader tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_SJIS);
			assertEquals(fileTest1a_SJIS, tfr.getFile());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#getEncoding()}.
	 */
	public void testGetEncoding() {
		TextFileReader tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_SJIS);
			assertNull(tfr.getEncoding());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_UTF8, "UTF-8");
			assertEquals("UTF-8", tfr.getEncoding());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#isOpen()}.
	 */
	public void testIsOpen() {
		TextFileReader tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_SJIS);
			assertTrue(tfr.isOpen());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		assertFalse(tfr.isOpen());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#close()}.
	 */
	public void testClose() {
		TextFileReader tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_SJIS);
			assertTrue(tfr.isOpen());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		assertFalse(tfr.isOpen());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#iterator()},
	 * {@link ssac.aadl.runtime.io.internal.AbTextFileReader#iterator()},
	 * {@link ssac.aadl.runtime.io.internal.AbTextFileReader#hasNext()},
	 * {@link ssac.aadl.runtime.io.internal.AbTextFileReader#next()}.
	 */
	public void testIteration() {
		TextFileReader tfr;
		
		// testData1
		//--- SJIS
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_SJIS);
			ArrayList<String> list = new ArrayList<String>();
			Iterator<String> it = tfr.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			assertEquals(Arrays.asList(testData1), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest1b_SJIS);
			ArrayList<String> list = new ArrayList<String>();
			for (String str : tfr) {
				list.add(str);
			}
			assertEquals(Arrays.asList(testData1), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		//--- UTF-8
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest1a_UTF8, "UTF-8");
			ArrayList<String> list = new ArrayList<String>();
			Iterator<String> it = tfr.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			assertEquals(Arrays.asList(testData1), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest1b_UTF8, "UTF-8");
			ArrayList<String> list = new ArrayList<String>();
			for (String str : tfr) {
				list.add(str);
			}
			assertEquals(Arrays.asList(testData1), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// testData2
		//--- SJIS
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest2a_SJIS);
			ArrayList<String> list = new ArrayList<String>();
			Iterator<String> it = tfr.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			assertEquals(Arrays.asList(testData2), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest2b_SJIS);
			ArrayList<String> list = new ArrayList<String>();
			for (String str : tfr) {
				list.add(str);
			}
			assertEquals(Arrays.asList(testData2), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		//--- UTF-8
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest2a_UTF8, "UTF-8");
			ArrayList<String> list = new ArrayList<String>();
			Iterator<String> it = tfr.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			assertEquals(Arrays.asList(testData2), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		tfr = null;
		try {
			tfr = new TextFileReader(fileTest2b_UTF8, "UTF-8");
			ArrayList<String> list = new ArrayList<String>();
			for (String str : tfr) {
				list.add(str);
			}
			assertEquals(Arrays.asList(testData2), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}
}
