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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)Files.java	0.10	2008/07/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package dtalge.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * ファイル操作に関する補助機能を提供するユーティリティクラス。
 * 
 * @version 0.10	2008/07/29
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.10
 *
 */
public final class Files
{
	private Files() {}
	
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//static private final int defBufferSize = 2048;

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
	
	/*
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
	 *
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
	*****/
	
	/*
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
	 *
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
	*****/

	/*
	 * 指定の <code>Reader</code> オブジェクトから、全てのテキストを
	 * 読み込み、読み込んだテキストを格納する
	 * <code>String</code> インスタンスを返す。
	 * 
	 * @param reader 読み込む <code>Reader</code> オブジェクト
	 * @return 読み込んだテキストを保持する <code>String</code> インスタンスを返す。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 *
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
	*****/
	
	/*
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
	 *
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
	*****/
	
	/*
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
	 *
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
	*****/

	/*
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
	 *
	static protected void readStringListFromReader(ArrayList<String> dist, BufferedReader reader)
		throws IOException
	{
		String strline;
		while ((strline = reader.readLine()) != null) {
			dist.add(strline);
		}
	}
	*****/
	
	/*
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
	 *
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
	*****/
	
	/*
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
	 *
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
	*****/
	
	/*
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
	 *
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
	*****/
	
	/*
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
	 *
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
	*****/

	/*
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
	 *
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
	*****/
}
