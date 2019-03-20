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
 *  Copyright 2007-2008  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)FileUtil.java	0.94	2008/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileUtil.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileUtil.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import exalge2.io.csv.CsvFormatException;
import exalge2.io.xml.XmlDocument;
import exalge2.io.xml.XmlDomParseException;

/**
 * ファイル操作に関する補助機能を提供するユーティリティクラス。
 * 
 * @version 0.94 2008/05/12
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
 *
 */
public class FileUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final int defBufferSize = 2048;

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定のストリームをクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param iStream クローズする <code>InputStream</code> オブジェクト
	 */
	static public void closeStream(InputStream iStream) {
		if (iStream != null) {
			try {
				iStream.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定の <code>Reader</code> をクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param iReader クローズする <code>Reader</code> オブジェクト
	 */
	static public void closeStream(Reader iReader) {
		if (iReader != null) {
			try {
				iReader.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定のストリームをクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param oStream クローズする <code>OutputStream</code> オブジェクト
	 */
	static public void closeStream(OutputStream oStream) {
		if (oStream != null) {
			try {
				oStream.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定の <code>Writer</code> をクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param oWriter クローズする <code>Writer</code> オブジェクト
	 */
	static public void closeStream(Writer oWriter) {
		if (oWriter != null) {
			try {
				oWriter.close();
			}
			catch (IOException ex) {}
		}
	}

	//------------------------------------------------------------
	// Text File I/O interfaces
	//------------------------------------------------------------
	
	/**
	 * テキストファイルを単一の文字列として読み込む。
	 * <p>
	 * このメソッドは、指定されたファイルをテキストファイルとしてデフォルトの
	 * 文字エンコーディングで全て読み込み、読み込んだテキストを格納する
	 * <code>String</code> インスタンスを返す。
	 * <br>
	 * ファイルに改行文字が含まれている場合、ファイルに記録されている改行文字の
	 * まま読み込む。
	 * 
	 * @param txtFile 読み込むテキストファイル
	 * 
	 * @return テキストファイルの内容を保持する <code>String</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.94
	 */
	static public String stringFromTextFile(File txtFile)
		throws FileNotFoundException, IOException
	{
		BufferedReader br = null;
		String strret;
		
		try {
			br = new BufferedReader(new FileReader(txtFile));
			
			strret = readStringFromReader(br);
		}
		finally {
			if (br != null) {
				closeStream(br);
				br = null;
			}
		}
		
		return strret;
	}
	
	/**
	 * 指定された文字セットで、テキストファイルを単一の文字列として読み込む。
	 * <p>
	 * このメソッドは、指定されたファイルをテキストファイルとして、指定の
	 * 文字エンコーディングで全て読み込み、読み込んだテキストを格納する
	 * <code>String</code> インスタンスを返す。
	 * <br>
	 * ファイルに改行文字が含まれている場合、ファイルに記録されている改行文字の
	 * まま読み込む。
	 * 
	 * @param txtFile 読み込むテキストファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return テキストファイルの内容を保持する <code>String</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.94
	 */
	static public String stringFromTextFile(File txtFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		InputStreamReader isr = null;
		BufferedReader br = null;
		String strret;
		
		try {
			isr = new InputStreamReader(new FileInputStream(txtFile), charsetName);
			br = new BufferedReader(isr);
			
			strret = readStringFromReader(br);
		}
		finally {
			if (br != null) {
				closeStream(br);
				br = null;
			}
			if (isr != null) {
				closeStream(isr);
				isr = null;
			}
		}
		
		return strret;
	}

	/**
	 * 指定の <code>Reader</code> オブジェクトから、全てのテキストを
	 * 読み込み、読み込んだテキストを格納する
	 * <code>String</code> インスタンスを返す。
	 * 
	 * @param reader 読み込む <code>Reader</code> オブジェクト
	 * @return 読み込んだテキストを保持する <code>String</code> インスタンスを返す。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.94
	 */
	static protected String readStringFromReader(Reader reader)
		throws IOException
	{
		char[] cbuf = new char[defBufferSize];
		StringBuilder sb = new StringBuilder();
		
		int len = 0;
		while ((len = reader.read(cbuf)) >= 0) {
			if (len > 0) {
				sb.append(cbuf, 0, len);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * テキストファイルを文字列のリストとして読み込む。
	 * <p>
	 * このメソッドは、指定されたファイルをテキストファイルとしてデフォルトの
	 * 文字エンコーディングで全て読み込み、読み込んだテキストを格納する
	 * <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * <br>テキストファイルを読み込む際、改行文字を要素の区切りとして使用する。
	 * このとき、要素の区切りとして使用した改行文字は破棄される。
	 * 
	 * @param txtFile 読み込むテキストファイル
	 * 
	 * @return テキストファイルの内容を保持する <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.94
	 */
	static public ArrayList<String> stringListFromTextFile(File txtFile)
		throws FileNotFoundException, IOException
	{
		BufferedReader br = null;
		ArrayList<String> ret = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new FileReader(txtFile));

			readStringListFromReader(ret, br);
		}
		finally {
			if (br != null) {
				closeStream(br);
				br = null;
			}
		}
		
		return ret;
	}
	
	/**
	 * 指定された文字セットで、テキストファイルを文字列のリストとして読み込む。
	 * <p>
	 * <p>
	 * このメソッドは、指定されたファイルをテキストファイルとして、指定の
	 * 文字エンコーディングで全て読み込み、読み込んだテキストを格納する
	 * <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * <br>テキストファイルを読み込む際、改行文字を要素の区切りとして使用する。
	 * このとき、要素の区切りとして使用した改行文字は破棄される。
	 * 
	 * @param txtFile 読み込むテキストファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return テキストファイルの内容を保持する <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.94
	 */
	static public ArrayList<String> stringListFromTextFile(File txtFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		InputStreamReader isr = null;
		BufferedReader br = null;
		ArrayList<String> ret = new ArrayList<String>();
		
		try {
			isr = new InputStreamReader(new FileInputStream(txtFile), charsetName);
			br = new BufferedReader(isr);
			
			readStringListFromReader(ret, br);
		}
		finally {
			if (br != null) {
				closeStream(br);
				br = null;
			}
			if (isr != null) {
				closeStream(isr);
				isr = null;
			}
		}
		
		return ret;
	}

	/**
	 * 指定の <code>Reader</code> オブジェクトから、全てのテキストを
	 * 読み込み、読み込んだテキストを格納する
	 * <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * <br>テキストファイルを読み込む際、改行文字を要素の区切りとして使用する。
	 * このとき、要素の区切りとして使用した改行文字は破棄される。
	 * 
	 * @param dist 文字列のリストを格納する <code>ArrayList&lt;String&gt;</code> オブジェクト
	 * @param reader 読み込む <code>BufferedReader</code> オブジェクト
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.94
	 */
	static protected void readStringListFromReader(ArrayList<String> dist, BufferedReader reader)
		throws IOException
	{
		String strline;
		while ((strline = reader.readLine()) != null) {
			dist.add(strline);
		}
	}
	
	/**
	 * 文字列を、デフォルトの文字エンコーディングでファイルに出力する。
	 * <br>
	 * 改行文字は、指定の文字列に含まれる文字のまま出力される。
	 * 
	 * @param source ファイルに出力する文字列
	 * @param txtFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.94
	 * 
	 */
	static public void stringToTextFile(String source, File txtFile)
		throws FileNotFoundException, IOException
	{
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(txtFile));
			pw = new PrintWriter(bw);
			
			pw.print(source);
			pw.flush();
		}
		finally {
			if (pw != null) {
				closeStream(pw);
				pw = null;
			}
			if (bw != null) {
				closeStream(bw);
				bw = null;
			}
		}
	}
	
	/**
	 * 文字列を、指定の文字エンコーディングでファイルに出力する。
	 * <br>
	 * 改行文字は、指定の文字列に含まれる文字のまま出力される。
	 * 
	 * @param source ファイルに出力する文字列
	 * @param txtFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.94
	 * 
	 */
	static public void stringToTextFile(String source, File txtFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			osw = new OutputStreamWriter(new FileOutputStream(txtFile), charsetName);
			bw = new BufferedWriter(osw);
			pw = new PrintWriter(bw);
			
			pw.print(source);
			pw.flush();
		}
		finally {
			if (pw != null) {
				closeStream(pw);
				pw = null;
			}
			if (bw != null) {
				closeStream(bw);
				bw = null;
			}
			if (osw != null) {
				closeStream(osw);
				osw = null;
			}
		}
	}
	
	/**
	 * 文字列のリストを、デフォルトの文字エンコーディングでファイルに出力する。
	 * ファイル出力の際、文字列リストの要素と要素の間に、自動的に改行文字を出力する。
	 * 要素の区切りとして出力される改行文字は、実行するプラットフォームに依存する。
	 * <br>
	 * なお、文字列の要素に改行文字が含まれている場合、そのままファイルに出力される。
	 * 
	 * @param source 文字列のコレクション
	 * @param txtFile	出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.94
	 * 
	 */
	static public void stringListToTextFile(Collection<String> source, File txtFile)
		throws FileNotFoundException, IOException
	{
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(txtFile));
			pw = new PrintWriter(bw);
			
			writeStringListToWriter(source, pw);
		}
		finally {
			if (pw != null) {
				closeStream(pw);
				pw = null;
			}
			if (bw != null) {
				closeStream(bw);
				bw = null;
			}
		}
	}
	
	/**
	 * 文字列のリストを、指定の文字エンコーディングでファイルに出力する。
	 * ファイル出力の際、文字列リストの要素と要素の間に、自動的に改行文字を出力する。
	 * 要素の区切りとして出力される改行文字は、実行するプラットフォームに依存する。
	 * <br>
	 * なお、文字列の要素に改行文字が含まれている場合、そのままファイルに出力される。
	 * 
	 * @param source 文字列のコレクション
	 * @param txtFile	出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.94
	 * 
	 */
	static public void stringListToTextFile(Collection<String> source, File txtFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			osw = new OutputStreamWriter(new FileOutputStream(txtFile), charsetName);
			bw = new BufferedWriter(osw);
			pw = new PrintWriter(bw);
			
			writeStringListToWriter(source, pw);
		}
		finally {
			if (pw != null) {
				closeStream(pw);
				pw = null;
			}
			if (bw != null) {
				closeStream(bw);
				bw = null;
			}
			if (osw != null) {
				closeStream(osw);
				osw = null;
			}
		}
	}

	/**
	 * 文字列のリストを、指定の <code>Writer</code> オブジェクトに出力する。
	 * 出力の際、文字列リストの要素と要素の間に、自動的に改行文字を出力する。
	 * 要素の区切りとして出力される改行文字は、実行するプラットフォームに依存する。
	 * <br>
	 * なお、文字列の要素に改行文字が含まれている場合、そのままファイルに出力される。
	 * 
	 * @param source	文字列のコレクション
	 * @param writer	書き込む <code>PrintWriter</code> オブジェクト
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.94
	 */
	static protected void writeStringListToWriter(Collection<String> source, PrintWriter writer)
		throws IOException
	{
		if (!source.isEmpty()) {
			Iterator<String> it = source.iterator();
			if (it.hasNext()) {
				writer.print(it.next());
				
				while (it.hasNext()) {
					writer.println();
					writer.flush();
					writer.print(it.next());
				}
				
				writer.flush();
			}
		}
	}

	//------------------------------------------------------------
	// CSV File I/O interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたクラスで指定された CSV ファイルを読み込み、
	 * 読み込みに成功したオブジェクトのインスタンスを返す。
	 * <p>
	 * 読み込み対象として指定するクラスには、次の読み込み用メソッドが正確に定義されていることが前提となる。
	 * <blockquote>
	 * <code>static public <i>実装クラスのインスタンス</i> fromCSV(File csvFile);</code>
	 * </blockquote>
	 * 読み込み用メソッドの戻り値は、実装クラスのインスタンスとなる。
	 * <p>
	 * フォーマットに適したオブジェクトが存在しない場合は、
	 * {@link UnsupportedFormatException} 例外をスローする。
	 * {@link UnsupportedFormatException} には、この例外の原因が含まれる場合がある。
	 * その場合、{@link UnsupportedFormatException#getCause()} で取得できる。
	 * 
	 * @param targetClasses 読み込みを実行するクラスの {@link java.lang.Class} インスタンスの配列
	 * @param csvFile CSV ファイル
	 * 
	 * @return 生成されたオブジェクトのインスタンス
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public Object newObjectFromCSV(Class[] targetClasses, File csvFile)
		throws UnsupportedFormatException, FileNotFoundException, IOException
	{
		CsvFormatException finalException = null;
		Object ret = null;
		
		for (int i = 0; i < targetClasses.length; i++) {
			// メソッド取得
			Method targetMethod = null;
			try {
				targetMethod = targetClasses[i].getMethod("fromCSV", File.class);
			}
			catch (SecurityException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (NoSuchMethodException ex) {
				throw new UnsupportedFormatException(ex);
			}
			
			// メソッド実行
			try {
				ret = targetMethod.invoke(null, csvFile);
			}
			catch (IllegalArgumentException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (IllegalAccessException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (InvocationTargetException ex) {
				Throwable exCause = ex.getCause();
				if (exCause instanceof FileNotFoundException) {
					throw ((FileNotFoundException)exCause);
				}
				else if (exCause instanceof IOException) {
					throw ((IOException)exCause);
				}
				else if (exCause instanceof CsvFormatException) {
					finalException = (CsvFormatException)exCause;
					ret = null;
				}
				else {
					throw new UnsupportedFormatException(exCause);
				}
			}
			
			// インスタンスチェック
			if (ret != null) {
				finalException = null;
				return ret;
			}
		}

		// Cannot read from File
		throw new UnsupportedFormatException(finalException);
	}
	
	/**
	 * 指定された文字セットで指定されたクラスで指定された CSV ファイルを読み込み、
	 * 読み込みに成功したオブジェクトのインスタンスを返す。
	 * <p>
	 * 読み込み対象として指定するクラスには、次の読み込み用メソッドが正確に定義されていることが前提となる。
	 * <blockquote>
	 * <code>static public <i>実装クラスのインスタンス</i> fromCSV(File csvFile, String charsetName);</code>
	 * </blockquote>
	 * 読み込み用メソッドの戻り値は、実装クラスのインスタンスとなる。
	 * <p>
	 * フォーマットに適したオブジェクトが存在しない場合は、
	 * {@link UnsupportedFormatException} 例外をスローする。
	 * {@link UnsupportedFormatException} には、この例外の原因が含まれる場合がある。
	 * その場合、{@link UnsupportedFormatException#getCause()} で取得できる。
	 * 
	 * @param targetClasses 読み込みを実行するクラスの {@link java.lang.Class} インスタンスの配列
	 * @param csvFile CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return 生成されたオブジェクトのインスタンス
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public Object newObjectFromCSV(Class[] targetClasses, File csvFile, String charsetName)
		throws UnsupportedFormatException, FileNotFoundException, IOException, UnsupportedEncodingException
	{
		CsvFormatException finalException = null;
		Object ret = null;
		
		for (int i = 0; i < targetClasses.length; i++) {
			// メソッド取得
			Method targetMethod = null;
			try {
				targetMethod = targetClasses[i].getMethod("fromCSV", File.class, String.class);
			}
			catch (SecurityException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (NoSuchMethodException ex) {
				throw new UnsupportedFormatException(ex);
			}
			
			// メソッド実行
			try {
				ret = targetMethod.invoke(null, csvFile, charsetName);
			}
			catch (IllegalArgumentException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (IllegalAccessException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (InvocationTargetException ex) {
				Throwable exCause = ex.getCause();
				if (exCause instanceof FileNotFoundException) {
					throw ((FileNotFoundException)exCause);
				}
				else if (exCause instanceof IOException) {
					throw ((IOException)exCause);
				}
				else if (exCause instanceof UnsupportedEncodingException) {
					throw ((UnsupportedEncodingException)exCause);
				}
				else if (exCause instanceof CsvFormatException) {
					finalException = (CsvFormatException)exCause;
					ret = null;
				}
				else {
					throw new UnsupportedFormatException(exCause);
				}
			}
			
			// インスタンスチェック
			if (ret != null) {
				finalException = null;
				return ret;
			}
		}

		// Cannot read from File
		throw new UnsupportedFormatException(finalException);
	}

	//------------------------------------------------------------
	// XML File I/O interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたクラスで指定された XML ファイルを読み込み、
	 * 読み込みに成功したオブジェクトのインスタンスを返す。
	 * <p>
	 * 読み込み対象として指定するクラスには、次の読み込み用メソッドが正確に定義されていることが前提となる。
	 * <blockquote>
	 * <code>static public <i>実装クラスのインスタンス</i> fromXML(File csvFile);</code>
	 * </blockquote>
	 * 読み込み用メソッドの戻り値は、実装クラスのインスタンスとなる。
	 * <p>
	 * フォーマットに適したオブジェクトが存在しない場合は、
	 * {@link UnsupportedFormatException} 例外をスローする。
	 * {@link UnsupportedFormatException} には、この例外の原因が含まれる場合がある。
	 * その場合、{@link UnsupportedFormatException#getCause()} で取得できる。
	 * 
	 * @param targetClasses 読み込みを実行するクラスの {@link java.lang.Class} インスタンスの配列
	 * @param xmlFile XML ファイル
	 * 
	 * @return 生成されたオブジェクトのインスタンス
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public Object newObjectFromXMLFile(Class[] targetClasses, File xmlFile)
		throws UnsupportedFormatException, IOException,
				FactoryConfigurationError, ParserConfigurationException
	{
		UnsupportedFormatException finalException = null;
		Object ret = null;
		
		for (int i = 0; i < targetClasses.length; i++) {
			// メソッド取得
			Method targetMethod = null;
			try {
				targetMethod = targetClasses[i].getMethod("fromXML", File.class);
			}
			catch (SecurityException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (NoSuchMethodException ex) {
				throw new UnsupportedFormatException(ex);
			}
			
			// メソッド実行
			try {
				ret = targetMethod.invoke(null, xmlFile);
			}
			catch (IllegalArgumentException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (IllegalAccessException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (InvocationTargetException ex) {
				Throwable exCause = ex.getCause();
				if (exCause instanceof FactoryConfigurationError) {
					throw ((FactoryConfigurationError)exCause);
				}
				else if (exCause instanceof ParserConfigurationException) {
					throw ((ParserConfigurationException)exCause);
				}
				else if (exCause instanceof IOException) {
					throw ((IOException)exCause);
				}
				else if (exCause instanceof SAXException) {
					finalException = new UnsupportedFormatException(exCause);
					ret = null;
				}
				else if (exCause instanceof XmlDomParseException) {
					finalException = new UnsupportedFormatException(exCause);
					ret = null;
				}
				else {
					throw new UnsupportedFormatException(exCause);
				}
			}
			
			// インスタンスチェック
			if (ret != null) {
				finalException = null;
				return ret;
			}
		}

		// Cannot read from File
		if (finalException != null)
			throw finalException;
		else
			throw new UnsupportedFormatException();
	}

	//------------------------------------------------------------
	// XML Document interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたクラスで指定された XML ドキュメントを解析し、
	 * 解析に成功したオブジェクトのインスタンスを返す。
	 * <p>
	 * 解析対象として指定するクラスには、次の解析用メソッドが正確に定義されていることが前提となる。
	 * <blockquote>
	 * <code>static public <i>実装クラスのインスタンス</i> fromXML(XmlDocument xmlDocument);</code>
	 * </blockquote>
	 * 解析用メソッドの戻り値は、実装クラスのインスタンスとなる。
	 * <p>
	 * フォーマットに適したオブジェクトが存在しない場合は、
	 * {@link UnsupportedFormatException} 例外をスローする。
	 * {@link UnsupportedFormatException} には、この例外の原因が含まれる場合がある。
	 * その場合、{@link UnsupportedFormatException#getCause()} で取得できる。
	 * 
	 * @param targetClasses 読み込みを実行するクラスの {@link java.lang.Class} インスタンスの配列
	 * @param xmldoc XML ドキュメント
	 * 
	 * @return 生成されたオブジェクトのインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws UnsupportedFormatException XML ドキュメントのフォーマットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public Object newObjectFromXmlDocument(Class[] targetClasses, XmlDocument xmldoc)
		throws UnsupportedFormatException
	{
		XmlDomParseException finalException = null;
		Object ret = null;
		
		if (xmldoc == null) {
			throw new NullPointerException("xmldoc is null!");
		}
		
		for (int i = 0; i < targetClasses.length; i++) {
			// メソッド取得
			Method targetMethod = null;
			try {
				targetMethod = targetClasses[i].getMethod("fromXML", XmlDocument.class);
			}
			catch (SecurityException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (NoSuchMethodException ex) {
				throw new UnsupportedFormatException(ex);
			}
			
			// メソッド実行
			try {
				ret = targetMethod.invoke(null, xmldoc);
			}
			catch (IllegalArgumentException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (IllegalAccessException ex) {
				throw new UnsupportedFormatException(ex);
			}
			catch (InvocationTargetException ex) {
				Throwable exCause = ex.getCause();
				if (exCause instanceof XmlDomParseException) {
					finalException = (XmlDomParseException)exCause;
					ret = null;
				}
				else {
					throw new UnsupportedFormatException(exCause);
				}
			}
			
			// インスタンスチェック
			if (ret != null) {
				finalException = null;
				return ret;
			}
		}

		// Cannot read from File
		throw new UnsupportedFormatException(finalException);
	}
}
