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
 *  Copyright 2007-2014  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)CsvWriter.java	0.984	2014/04/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvWriter.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvWriter.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import exalge2.io.FileUtil;
import exalge2.util.Strings;

/**
 * CSV ファイルへの書き込み機能を提供する Writer クラス。
 * <br>
 * 基本的にカンマ(<code>,</code>)区切りのテキストファイルを出力する。
 * <p>
 * <strong>(注)</strong> このクラスは、標準 Java {@link java.io.Writer Writer} インタフェースの
 * 拡張ではない。
 * <p>
 * 
 * @version 0.984	2014/04/28
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
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
	 * {@link java.io.Writer} インスタンス
	 */
	private Writer csvWriter;
	//private BufferedWriter csvWriter;

	/**
	 * 次に書き込みを行うCSVのフィールド番号。この番号は、1 から始まる。
	 */
	private int csvFieldNo = 1;
	/**
	 * このプラットフォームの改行文字
	 */
	private String	_lineSeparator;

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
		_lineSeparator = _getLineSeparator();
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
			FileUtil.closeStream(foStream);
			throw ex;
		}
		catch (RuntimeException ex) {
			FileUtil.closeStream(foStream);
			throw ex;
		}
		// setup BufferedWriter
		this.csvWriter = new BufferedWriter(osWriter);
		_lineSeparator = _getLineSeparator();
	}
	
	/**
	 * 指定された文字型出力ストリームで、新規 <code>CsvWriter</code> インスタンスを生成する。
	 * 
	 * @param writer 書き込みに使用する文字型出力ストリーム
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public CsvWriter(Writer writer) {
		if (writer==null)
			throw new NullPointerException("'writer' argument cannot be null.");
		this.csvWriter = writer;
		_lineSeparator = _getLineSeparator();
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
		if (delimiter=='"')
			throw new IllegalArgumentException("Illegal delimiter : " + String.valueOf(delimiter));
		this.csvDelimiter = delimiter;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ストリームを閉じる。
	 */
	public void close() {
		FileUtil.closeStream(this.csvWriter);
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
		//this.csvWriter.newLine();
		this.csvWriter.write(_lineSeparator);
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
	 * 現在の出力位置にコメント行を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * <br>
	 * 現在位置が行先頭ではない場合、現在位置に改行文字を出力した後にコメント行を出力する。
	 * <p>
	 * コメント行は、行(レコード)の先頭が <code>'#'</code> 文字で始まる行を示す。
	 * 
	 * @param strComment 出力するコメント文字列(改行を含めてはならない)
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws IllegalArgumentException	指定された文字列に改行文字が含まれている場合
	 */
	public void writeCommentLine(String strComment)
		throws IOException
	{
		// check
		if (Strings.contains(strComment, '\n', '\r'))
			throw new IllegalArgumentException("Included line separator character in specified string.");

		// New line
		if (this.csvFieldNo > 1) {
			newLine();
		}
		
		// Write comment line
		this.csvWriter.append('#');
		if (strComment != null)
			this.csvWriter.write(strComment);
		newLine();
	}

	/**
	 * 現在の出力位置に、指定された文字列を１行分の文字列として出力する。
	 * フィールド位置情報は、先頭フィールドへリセットされる。また、指定の文字列を出力後、
	 * 自動的に改行文字も出力する。
	 * <br>
	 * 現在位置が先頭フィールドではない場合、現在位置に改行文字を出力した後に空行を出力する。
	 * <p><b>注:</b>このメソッドは、指定された文字列をそのまま出力するので、エンクオートされない。
	 * <p>指定された文字列が <tt>null</tt> の場合は、空行が出力される。
	 * 
	 * @param lineValue 出力する文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.982
	 */
	public void writeLine(String lineValue) throws IOException
	{
		if (this.csvFieldNo > 1) {
			newLine();
		}
		if (lineValue != null) {
			this.csvWriter.write(lineValue);
		}
		this.newLine();
	}

	/**
	 * 現在の出力位置に、指定された文字列を１行分の文字列として出力する。
	 * フィールド位置情報は、先頭フィールドへリセットされる。また、指定の文字列を出力後、
	 * 自動的に改行文字も出力する。
	 * <br>
	 * 現在位置が先頭フィールドではない場合、現在位置に改行文字を出力した後に空行を出力する。
	 * <p><b>(注)</b>このメソッドは、指定された文字列をそのまま出力するので、エンクオートされない。
	 * <p>指定されたフォーマット文字列が <tt>null</tt> の場合は、例外をスローする。
	 * 
	 * @param format	書式文字列(参考：{@link java.lang.String#format(String, Object[])})
	 * @param args		書式文字列の書式指示子により参照される引数(参考：{@link java.lang.String#format(String, Object[])})
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @see java.lang.String#format(String, Object[])
	 * 
	 * @since 0.982
	 */
	public void writeLine(String format, Object...args) throws IOException {
		writeLine(String.format(format, args));
	}
	
	/**
	 * 現在位置にフィールド文字列を出力する。
	 * <br>
	 * これから書き込みを行うフィールド位置がレコード先頭(行頭)の場合は、指定された文字列がそのまま出力される。
	 * これから書き込みを行うフィールド位置がレコード先頭(行頭)ではない場合は、フィールド区切り文字を出力してから
	 * 指定された文字列が出力される。
	 * ここで出力したフィールドをレコード終端とする場合は、{@link #newLine()} を呼び出し改行を出力する。
	 * <br>
	 * 指定された文字列が <tt>null</tt> の場合は、空文字列のフィールドとして出力される。
	 * <p>
	 * なお、このメソッドでは、必要に応じてエンクオートする。
	 * 
	 * @param strValue 出力するフィールド文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.982
	 */
	public void writeField(String strValue) throws IOException {
		// レコード先頭でなければ、フィールド区切り文字を出力
		if (csvFieldNo > 1) {
			csvWriter.append(csvDelimiter);
		}
		
		// output field
		if (strValue != null)
			csvWriter.write(enquote(strValue));
		csvFieldNo++;
	}
	
	/**
	 * 現在位置にフィールド文字列を出力する。
	 * <br>
	 * これから書き込みを行うフィールド位置がレコード先頭(行頭)の場合は、指定された文字列がそのまま出力される。
	 * これから書き込みを行うフィールド位置がレコード先頭(行頭)ではない場合は、フィールド区切り文字を出力してから
	 * 指定された文字列が出力される。
	 * ここで出力したフィールドをレコード終端とする場合は、{@link #newLine()} を呼び出し改行を出力する。
	 * <br>
	 * 指定されたフォーマット文字列が <tt>null</tt> の場合は、例外をスローする。
	 * <p>
	 * なお、このメソッドでは、必要に応じてエンクオートする。
	 * 
	 * @param format	書式文字列(参考：{@link java.lang.String#format(String, Object[])})
	 * @param args		書式文字列の書式指示子により参照される引数(参考：{@link java.lang.String#format(String, Object[])})
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @see java.lang.String#format(String, Object[])
	 * 
	 * @since 0.982
	 */
	public void writeField(String format, Object...args) throws IOException {
		writeField(String.format(format, args));
	}

	/**
	 * @deprecated このメソッドは互換性のためだけに残されています。新しい機能 {@link #writeField(String)} を使用してください。
	 * 
	 * 現在位置にカラム文字列を出力する。
	 * <br>
	 * <code>isFinalColumn</code> が <tt>false</tt> の場合は、カラム文字列出力後に
	 * カラム区切り文字を出力し、カラム位置情報をインクリメントする。
	 * <br>
	 * <code>isFinalColumn</code> が <tt>true</tt> の場合は、カラム文字列出力後に
	 * 改行文字を出力し、カラム位置情報を先頭カラムへリセットする。
	 * 
	 * @param strValue 出力するカラム文字列
	 * @param isFinalColumn 出力したカラムをレコード終端とする場合は <tt>true</tt>
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeColumn(String strValue, boolean isFinalColumn) throws IOException
	{
		// レコード先頭でなければ、フィールド区切り文字を出力
		if (csvFieldNo > 1) {
			csvWriter.append(csvDelimiter);
		}
		
		// output field
		if (strValue != null)
			csvWriter.write(strValue);
		csvFieldNo++;
		
		// final column
		if (isFinalColumn) {
			newLine();
		}
	}

	/**
	 * @deprecated このメソッドは互換性のためだけに残されています。新しい機能 {@link #writeField(String)} を使用してください。
	 * 
	 * 現在位置にカラム文字列を出力する。出力する際、カラム文字列はダブルクオートでエンコードされる。
	 * <br>
	 * <code>isFinalColumn</code> が <tt>false</tt> の場合は、カラム文字列出力後に
	 * カラム区切り文字を出力し、カラム位置情報をインクリメントする。
	 * <br>
	 * <code>isFinalColumn</code> が <tt>true</tt> の場合は、カラム文字列出力後に
	 * 改行文字を出力し、カラム位置情報を先頭カラムへリセットする。
	 * 
	 * @param strValue 出力するカラム文字列
	 * @param isFinalColumn 出力したカラムをレコード終端とする場合は <tt>true</tt>
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeColumnWithQuote(String strValue, boolean isFinalColumn) throws IOException
	{
		String strEncodedValue;
		if (strValue != null) {
			strEncodedValue = encodeToken(strValue);
		} else {
			strEncodedValue = "\"\"";
		}
		writeColumn(strEncodedValue, isFinalColumn);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected String _getLineSeparator() {
		return System.getProperty("line.separator");
	}

	/**
	 * 文字列をダブルクオートでエンクオートする。
	 * このメソッドは、フィールド区切り文字、改行文字、ダブルクオートが含まれている場合のみ、
	 * エンクオートした文字列を返す。それ以外の場合は、入力文字列をそのまま返す。
	 * 
	 * @param srcString エンクオートする文字列
	 * @return エンクオートした文字列
	 * 
	 * @since 0.982
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

	/**
	 * @deprecated このメソッドは {@link #enquote(String)} に置き換えられました。
	 * 
	 * 文字列をダブルクオートでエンコードする。
	 * 
	 * @param srcString エンコードする文字列
	 * 
	 * @return エンコードした文字列
	 */
	protected String encodeToken(final String srcString) {
		final StringBuffer sb = new StringBuffer();
		sb.append('"');
		sb.append(srcString.replaceAll("\"", "\"\""));
		sb.append('"');
		return sb.toString();
	}
}
