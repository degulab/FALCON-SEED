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
 * @(#)TextFileWriterTest.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.io.TextFileWriter} クラスのテスト。
 * 
 * @version 1.50	2010/09/27
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.50
 */
public class TextFileWriterTest extends TestCase
{
	static protected File basedir = new File("testdata/unittest/runtime/text");
	static protected File fileTest_SJIS = new File(basedir, "outputData_sjis.txt");
	static protected File fileTest_UTF8 = new File(basedir, "outputData_utf8.txt");
	static protected File notexistFile = new File("testdata/unittest/runtime/nodir/testData0.txt");
	
	static protected String[] testData = {
		"1. はじめに",
		"AADL（Algebraic Accounting Description Language）は、交換代数による代数的上位仕様記述を"
		+ "行うため、領域固有言語（DSL）のプロトタイプとして開発された言語です。AADL は専用のコンパイラー"
		+ "（AADL コンパイラー）により、AADL 実行モジュールにコンパイルされます。",
		"AADL コンパイラーは、AADL プログラムコードをJAVA プログラムとして実行可能なモジュールにコン"
		+ "パイルします。そのため、AADL 実行モジュールはJAVA プログラムとしてJAVA 実行環境の上で動作し"
		+ "ます。AADL コンパイラーは、JAVA により実装されています。",
		"本書では、AADL の言語仕様について説明します。",
		"",
		"1.1. AADL コンパイラーの構成",
		"AADL コンパイラーは、交換代数に基づいた言語仕様により記述されたAADL プログラムを入力とし、"
		+ "構文解析、意味解析を行った結果からJAVA プログラムとして実行可能なモジュール（Jar ファイル）を出"
		+ "力するプリプロセッサーです。",
		"交換代数に関する機能の実装として、交換代数パッケージ（Exalge2.jar）を利用しています。",
		"AADL コンパイラーの構文解析に関する実装として、オープンソース・ソフトウェアであるANTLR"
		+ "（http://www.antlr.org/）により生成されたJAVA コードを使用しています。ANTLR は、BNF 的な専用"
		+ "の文法定義ファイルからパーサーとして機能するJAVAプログラムコードを生成するパーサー・ジェネレー"
		+ "タとして公開されています。",
		"ANTLR、交換代数コア・パッケージを利用したAADL コンパイラーの構成は、次の通りです。",
		"AADL コンパイラーによるコンパイルにおいては、AADL プログラムコードがパーサーにより解析された"
		+ "結果からJAVA プログラムコードを生成します。このとき、交換代数に関する記述は、交換代数パッケージ"
		+ "のクラス・メソッド呼び出しに変換されます。生成されたJAVA プログラムコードは、Sun Microsystems 社"
		+ "が提供するJAVA コンパイラー（JAVA 1.5 以上）によってバイトコードにコンパイルされ、最終的にJAVA"
		+ "実行モジュール（Jar ファイル）として出力されます。",
		"",
		"1.3. 注意事項",
		"AADL で記述したプログラムコードから生成される実行モジュールはJAVA 実行環境上で動作するため、",
		"基本的にJAVA言語仕様の制約、ならびにJAVA実行環境の制約に準じます。",
		"AADL で規定されるデータ型もJAVA 標準のクラス、交換代数パッケージで提供されるクラスの仕様に準じます。",		
	};

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.TextFileWriter#TextFileWriter(java.io.File)}.
	 */
	public void testTextFileWriterFile() {
		// null
		try {
			new TextFileWriter(null);
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		
		// normal
		TextFileWriter tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_SJIS);
		}
		catch (Throwable ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// file not found
		tfr = null;
		try {
			tfr = new TextFileWriter(notexistFile);
			fail("must be throw FileNotFoundException.");
		}
		catch (FileNotFoundException ex) {
			System.out.println("Throw FileNotFoundException by TextFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.TextFileWriter#TextFileWriter(java.io.File, java.lang.String)}.
	 */
	public void testTextFileWriterFileString() {
		// null
		try {
			new TextFileWriter(null, "MS932");
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		try {
			new TextFileWriter(fileTest_UTF8, null);
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		
		// normal
		TextFileWriter tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_UTF8, "UTF-8");
		}
		catch (Throwable ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// file not found
		tfr = null;
		try {
			tfr = new TextFileWriter(notexistFile, "UTF-8");
			fail("must be throw FileNotFoundException.");
		}
		catch (FileNotFoundException ex) {
			System.out.println("Throw FileNotFoundException by TextFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// unsupported encoding
		tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_UTF8, "hoge");
			fail("must be throw UnsupportedEncodingException.");
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			System.out.println("Throw UnsupportedEncodingException by TextFileWriter constructer: " + ex.toString());
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
		TextFileWriter tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_SJIS);
			assertEquals(fileTest_SJIS, tfr.getFile());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
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
		TextFileWriter tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_SJIS);
			assertNull(tfr.getEncoding());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_UTF8, "UTF-8");
			assertEquals("UTF-8", tfr.getEncoding());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
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
		TextFileWriter tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_SJIS);
			assertTrue(tfr.isOpen());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
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
		TextFileWriter tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_SJIS);
			assertNull(tfr.lastException());
			tfr.close();
			tfr.flush();
			assertNotNull(tfr.lastException());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
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
		TextFileWriter tfr = null;
		try {
			tfr = new TextFileWriter(fileTest_SJIS);
			assertTrue(tfr.isOpen());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
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
		TextFileWriter writer;
		TextFileReader reader;
		
		// sjis
		//--- write
		writer = null;
		try {
			writer = new TextFileWriter(fileTest_SJIS);
			for (int i = 0; i < testData.length; i++) {
				writer.print(testData[i]);
				writer.newRecord();
				//---
				i++;
				if (i >= testData.length)
					break;
				writer.print(testData[i]);
				writer.newLine();
				//---
				i++;
				if (i >= testData.length)
					break;
				writer.print(testData[i]);
				writer.println();
				//---
				i++;
				if (i >= testData.length)
					break;
				writer.println(testData[i]);
			}
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		finally {
			if (writer != null) {
				writer.close();
			}
		}
		//--- check
		reader = null;
		try {
			reader = new TextFileReader(fileTest_SJIS);
			ArrayList<String> list = new ArrayList<String>();
			for (String rec : reader) {
				list.add(rec);
			}
			assertEquals(Arrays.asList(testData), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
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
			writer = new TextFileWriter(fileTest_UTF8, "UTF-8");
			for (int i = 0; i < testData.length; i++) {
				writer.print(testData[i]);
				writer.newRecord();
				//---
				i++;
				if (i >= testData.length)
					break;
				writer.print(testData[i]);
				writer.newLine();
				//---
				i++;
				if (i >= testData.length)
					break;
				writer.print(testData[i]);
				writer.println();
				//---
				i++;
				if (i >= testData.length)
					break;
				writer.println(testData[i]);
			}
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileWriter constructer: " + ex.toString());
		}
		finally {
			if (writer != null) {
				writer.close();
			}
		}
		//--- check
		reader = null;
		try {
			reader = new TextFileReader(fileTest_UTF8, "UTF-8");
			ArrayList<String> list = new ArrayList<String>();
			for (String rec : reader) {
				list.add(rec);
			}
			assertEquals(Arrays.asList(testData), list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by TextFileReader constructer: " + ex.toString());
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
