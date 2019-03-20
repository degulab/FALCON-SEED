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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvWriter.java	1.00	2008/10/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import static ssac.util.Validations.validNotNull;
import static ssac.util.Validations.validArgument;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import ssac.util.Strings;

/**
 * CSV ファイルへの書き込み機能を提供する Writer クラス。
 * <br>
 * 基本的にカンマ(<code>,</code>)区切りのテキストファイルを出力する。
 * <p>
 * <strong>(注)</strong> このクラスは、標準 Java {@link java.io.Writer Writer} インタフェースの
 * 拡張ではない。
 * <p>
 * 
 * @version 1.00	2008/10/06
 * 
 * @since 1.00
 */
public class CsvWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * カラム区切り文字
	 */
	private char csvDelimiter = ',';
	/**
	 * {@link java.io.BufferedWriter} インスタンス
	 */
	private BufferedWriter csvWriter;

	/**
	 * 次に書き込みを行うCSVのフィールド番号。この番号は、1 から始まる。
	 */
	private int csvFieldNo = 1;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 書き込み先のファイルにデフォルト文字セットで出力する、新規 <code>CsvWriter</code> インスタンスを生成する。
	 * 
	 * @param csvFile 書き込み先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public CsvWriter(File csvFile)
		throws FileNotFoundException, IOException
	{
		FileWriter fWriter = new FileWriter(csvFile);
		this.csvWriter = new BufferedWriter(fWriter);
	}
	
	/**
	 * 書き込み先ファイルに指定された文字セットで出力する、新規 <code>CsvWriter</code> インスタンスを生成する。
	 * 
	 * @param csvFile 書き込み先ファイル
	 * @param encoding 文字セット名
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public CsvWriter(File csvFile, String encoding)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		// create Writer
		FileOutputStream foStream = new FileOutputStream(csvFile);
		OutputStreamWriter osWriter = null;
		try {
			osWriter = new OutputStreamWriter(foStream, encoding);
		}
		catch (UnsupportedEncodingException ex) {
			Files.closeStream(foStream);
			throw ex;
		}
		catch (RuntimeException ex) {
			Files.closeStream(foStream);
			throw ex;
		}
		// setup BufferedWriter
		this.csvWriter = new BufferedWriter(osWriter);
	}
	
	/**
	 * 指定された文字型出力ストリームで、新規 <code>CsvWriter</code> インスタンスを生成する。
	 * 
	 * @param writer 書き込みに使用する文字型出力ストリーム
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public CsvWriter(BufferedWriter writer) {
		this.csvWriter = validNotNull(writer, "'writer' argument cannot be null.");
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 現在のフィールド区切り文字を返す。
	 * 
	 * @return フィールド区切り文字
	 */
	public char getDelimiterChar() {
		return this.csvDelimiter;
	}

	/**
	 * フィールド区切り文字として認識する文字を設定する。
	 * <br>
	 * <code>CsvReader</code> は、このメソッドにより指定された文字をフィールド区切り文字として、
	 * フィールド読み込みを行う。<br>
	 * なお、この区切り文字には半角ダブルクオートは指定できない。
	 * 
	 * @param delimiter 設定する区切り文字
	 * 
	 * @throws IllegalArgumentException 区切り文字に無効な文字が指定された場合
	 */
	public void setDelimiterChar(final char delimiter) {
		validArgument(delimiter != '"', "Illegal delimiter : " + String.valueOf(delimiter));
		this.csvDelimiter = delimiter;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ストリームを閉じる。
	 */
	public void close() {
		Files.closeStream(this.csvWriter);
	}

	/**
	 * ストリームをフラッシュする。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void flush() throws IOException
	{
		this.csvWriter.flush();
	}

	/**
	 * 現在の出力位置に改行文字を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void newLine() throws IOException
	{
		this.csvWriter.newLine();
		this.csvFieldNo = 1;
	}

	/**
	 * 現在の出力位置に空行を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * <br>
	 * 現在位置が先頭フィールドではない場合、現在位置に改行文字を出力した後に空行を出力する。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeBlankLine() throws IOException
	{
		if (this.csvFieldNo > 1) {
			newLine();
		}
		newLine();
	}

	/**
	 * 現在の出力位置に、指定された文字列を１行分の文字列として出力する。
	 * フィールド位置情報は、先頭フィールドへリセットされる。また、指定の文字列を出力後、
	 * 自動的に改行文字も出力する。
	 * <br>
	 * 現在位置が先頭フィールドではない場合、現在位置に改行文字を出力した後に空行を出力する。
	 * <p><b>注:</b>このメソッドは、指定された文字列をそのまま出力するので、エンクオートされない。
	 * 
	 * @param lineValue 出力する文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeLine(String lineValue) throws IOException
	{
		if (this.csvFieldNo > 1) {
			newLine();
		}
		this.csvWriter.write(lineValue);
		this.newLine();
	}

	/**
	 * 現在の出力位置に、指定された文字列を１行分の文字列として出力する。
	 * フィールド位置情報は、先頭フィールドへリセットされる。また、指定の文字列を出力後、
	 * 自動的に改行文字も出力する。
	 * <br>
	 * 現在位置が先頭フィールドではない場合、現在位置に改行文字を出力した後に空行を出力する。
	 * <p><b>(注)</b>このメソッドは、指定された文字列をそのまま出力するので、エンクオートされない。
	 * 
	 * @param format	書式文字列(参考：{@link java.lang.String#format(String, Object[])})
	 * @param args		書式文字列の書式指示子により参照される引数(参考：{@link java.lang.String#format(String, Object[])})
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @see java.lang.String#format(String, Object[])
	 */
	public void writeLine(String format, Object...args) throws IOException {
		writeLine(String.format(format, args));
	}
	
	/**
	 * 現在位置にフィールド文字列を出力する。
	 * <br>
	 * <code>isFinalField</code> が <tt>false</tt> の場合は、フィールド文字列出力後に
	 * フィールド区切り文字を出力し、フィールド位置情報をインクリメントする。
	 * <br>
	 * <code>isFinalField</code> が <tt>true</tt> の場合は、フィールド文字列出力後に
	 * 改行文字を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * <p>
	 * なお、このメソッドでは、必要に応じてエンクオートする。
	 * 
	 * @param isFinalField 出力したフィールドをレコード終端とする場合は <tt>true</tt>
	 * @param strValue 出力するフィールド文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeField(boolean isFinalField, String strValue) throws IOException {
		validNotNull(strValue, "'strValue' argument cannot be null.");
		
		// output Field
		this.csvWriter.write(enquote(strValue));
		
		// フィールド終端
		if (isFinalField) {
			// レコード終端は改行
			newLine();
		} else {
			// フィールド終端は、デリミタ
			this.csvWriter.write(this.csvDelimiter);
			this.csvFieldNo++;
		}
	}
	
	/**
	 * 現在位置にフィールド文字列を出力する。
	 * <br>
	 * <code>isFinalField</code> が <tt>false</tt> の場合は、フィールド文字列出力後に
	 * フィールド区切り文字を出力し、フィールド位置情報をインクリメントする。
	 * <br>
	 * <code>isFinalField</code> が <tt>true</tt> の場合は、フィールド文字列出力後に
	 * 改行文字を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * <p>
	 * なお、このメソッドでは、必要に応じてエンクオートする。
	 * 
	 * @param isFinalField 出力したフィールドをレコード終端とする場合は <tt>true</tt>
	 * @param format	書式文字列(参考：{@link java.lang.String#format(String, Object[])})
	 * @param args		書式文字列の書式指示子により参照される引数(参考：{@link java.lang.String#format(String, Object[])})
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @see java.lang.String#format(String, Object[])
	 */
	public void writeField(boolean isFinalField, String format, Object...args) throws IOException {
		writeField(isFinalField, String.format(format, args));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 文字列をダブルクオートでエンクオートする。
	 * このメソッドは、フィールド区切り文字、改行文字、ダブルクオートが含まれている場合のみ、
	 * エンクオートした文字列を返す。それ以外の場合は、入力文字列をそのまま返す。
	 * 
	 * @param srcString エンクオートする文字列
	 * @return エンクオートした文字列
	 */
	protected String enquote(final String srcString) {
		String retString = srcString;
		if (!Strings.isNullOrEmpty(srcString) && Strings.contains(srcString, csvDelimiter, '"', '\r', '\n')) {
			StringBuilder sb = new StringBuilder(srcString.length() * 2);
			sb.append('"');
			sb.append(srcString.replaceAll("\"", "\"\""));
			sb.append('"');
			retString = sb.toString();
		}
		return retString;
	}
}
