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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)OutputLogWriter.java	3.0.0	2014/03/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * 標準出力、標準エラー出力の内容を、指定されたログファイルに書き込むクラス。
 * <p>ログファイルの構造はCSVファイルであり、第１列は種別、第２列はデータとなる。
 * 種別には、標準出力(=1)、標準エラー出力(=2)のどちらかを記述する。
 * データは改行なども含まれるため、基本的にダブルクオートでエスケープする。
 * 
 * @version 3.0.0	2014/03/11
 * @since 3.0.0
 */
public class OutputLogWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 出力先ファイルの抽象パス **/
	private final File	_logFile;

	/** 同期用オブジェクト **/
	protected Object	_lock = new Object();
	/** ファイルへ出力するライターオブジェクト **/
	protected BufferedWriter	_writer;
	/** このオブジェクトのエンコーディングに使用される文字セット名 **/
	private String		_encoding;
	/** 入出力エラーが発生した場合 **/
	private boolean		_trouble;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの文字セットを使用する <code>OutputLogWriter</code> を生成する。
	 * @param logFile	ログ出力先の抽象パス
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルがディレクトリである場合、ファイルが作成できない場合、または何らかの理由で開くことができない場合
	 * @throws SecurityException		セキュリティーマネージャーが存在する場合に、セキュリティーマネージャーの <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを許可しない場合
	 */
	public OutputLogWriter(File logFile) throws FileNotFoundException
	{
		if (logFile == null)
			throw new NullPointerException("Log file object is null.");
		_logFile = logFile;
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(logFile, true));	// append mode
		init(osw, osw.getEncoding());
	}

	/**
	 * 指定された文字セットを使用する <code>OutputLogWriter</code> を生成する。
	 * @param logFile	ログ出力先の抽象パス
	 * @param encoding	文字セット
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルがディレクトリである場合、ファイルが作成できない場合、または何らかの理由で開くことができない場合
	 * @throws SecurityException		セキュリティーマネージャーが存在する場合に、セキュリティーマネージャーの <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを許可しない場合
	 */
	public OutputLogWriter(File logFile, Charset encoding) throws FileNotFoundException
	{
		if (logFile == null)
			throw new NullPointerException("Log file object is null.");
		_logFile = logFile;
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(logFile, true), encoding);	// append mode
		init(osw, osw.getEncoding());
	}

	/**
	 * 指定された文字セットを使用する <code>OutputLogWriter</code> を生成する。
	 * @param logFile	ログ出力先の抽象パス
	 * @param encoding	文字セット名
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルがディレクトリである場合、ファイルが作成できない場合、または何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException	指定された文字エンコーディングがサポートされていない場合
	 * @throws SecurityException		セキュリティーマネージャーが存在する場合に、セキュリティーマネージャーの <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを許可しない場合
	 */
	public OutputLogWriter(File logFile, String encoding) throws FileNotFoundException, UnsupportedEncodingException
	{
		if (logFile == null)
			throw new NullPointerException("Log file object is null.");
		_logFile = logFile;
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(logFile, true), encoding);	// append mode
		init(osw, (encoding==null ? osw.getEncoding() : encoding));
	}

	/**
	 * このオブジェクト固有の初期化。
	 * このメソッドはコンストラクタから呼び出される。
	 * @param writer			ライターオブジェクト
	 * @param actualEncoding	実際のエンコーディングとなる文字セット名
	 */
	private void init(Writer writer, String actualEncoding) {
		_writer = new BufferedWriter(writer);
		_encoding = actualEncoding;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトが出力先としているファイルの抽象パスを返す。
	 * @return	出力先ファイルの抽象パス
	 */
	public File getFile() {
		return _logFile;
	}

	/**
	 * このオブジェクトのエンコーディングに使用されている文字セット名を返す。
	 * @return	エンコーディングの文字セット名
	 */
	public String getEncoding() {
		return _encoding;
	}

	/**
	 * ストリームをフラッシュし、そのエラー状況を確認する。
	 * 基本となる出力ストリームが <code>InterruptedIOException</code> ではなく <code>IOException</code> をスローした場合、
	 * および <code>setError</code> メソッドが呼び出された場合に、内部エラー状態は <tt>true</tt> に設定される。
	 * 基本となる出力ストリームのオペレーションが <code>InterruptedIOException</code> をスローすると、
	 * 次の操作によって例外を割り込みに戻す。
	 * <blockquote>
	 * <code>Thread.currentThread().interrupt();</code>
	 * </blockquote>
	 * @return	エラー状態であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean checkError() {
		if (_writer != null) {
			flush();
		}
		return _trouble;
	}

	/**
	 * ストリームをフラッシュする。
	 */
	public void flush() {
		try {
			synchronized (_lock) {
				ensureOpen();
				_writer.flush();
			}
		}
		catch (IOException ex) {
			_trouble = true;
		}
	}

	/**
	 * ストリームをフラッシュし、ストリームを閉じる。
	 */
	public void close() {
		try {
			synchronized (_lock) {
				if (_writer == null)
					return;
				_writer.close();
				_writer = null;
			}
		}
		catch (IOException ex) {
			_trouble = true;
		}
	}

	/**
	 * 文字列を出力する。
	 * 引数が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * <tt>null</tt> でない場合は、指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param str		出力する文字列
	 */
	public void print(boolean isError, String str) {
		writeString(isError, (str==null ? "null" : str), false);
	}
	
	/**
	 * 指定されたオブジェクトを出力する。
	 * オブジェクトは {@link java.lang.String#valueOf(Object)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param str		出力するオブジェクト
	 */
	public void print(boolean isError, Object obj) {
		writeString(isError, String.valueOf(obj), false);
	}

	/**
	 * 指定された書式付き文字列を出力する。
	 * 書式付き文字列は、{@link java.lang.String#format(String, Object...)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 */
	public void printf(boolean isError, String format, Object...args) {
		writeString(isError, String.format(format, args), false);
	}

	/**
	 * 指定された書式付き文字列を出力する。
	 * 書式付き文字列は、{@link java.lang.String#format(Locale, String, Object...)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param l			書式設定時に適用する <code>Locale</code>。<tt>null</tt> の場合、ローカリゼーションは適用されない。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 */
	public void printf(boolean isError, Locale l, String format, Object...args) {
		writeString(isError, String.format(l, format, args), false);
	}
	
	/**
	 * 文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * 引数が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * <tt>null</tt> でない場合は、指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param str		出力する文字列
	 */
	public void println(boolean isError, String str) {
		writeString(isError, (str==null ? "null" : str), true);
	}
	
	/**
	 * 指定されたオブジェクトを出力し、プラットフォーム固有の改行文字を出力する。
	 * オブジェクトは {@link java.lang.String#valueOf(Object)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param str		出力するオブジェクト
	 */
	public void println(boolean isError, Object obj) {
		writeString(isError, String.valueOf(obj), true);
	}
	
	/**
	 * 指定された書式付き文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * 書式付き文字列は、{@link java.lang.String#format(String, Object...)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 */
	public void printfln(boolean isError, String format, Object...args) {
		writeString(isError, String.format(format, args), true);
	}
	
	/**
	 * 指定された書式付き文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * 書式付き文字列は、{@link java.lang.String#format(Locale, String, Object...)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param l			書式設定時に適用する <code>Locale</code>。<tt>null</tt> の場合、ローカリゼーションは適用されない。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 */
	public void printfln(boolean isError, Locale l, String format, Object...args) {
		writeString(isError, String.format(l, format, args), true);
	}

	/**
	 * 標準出力の内容として、文字列を出力する。
	 * @param str		出力する文字列
	 * @see #print(boolean, String)
	 */
	public void outPrint(String str) {
		print(false, str);
	}
	
	/**
	 * 標準出力の内容として、指定されたオブジェクトを出力する。
	 * @param str		出力するオブジェクト
	 * @see #print(boolean, Object)
	 */
	public void outPrint(Object obj) {
		print(false, obj);
	}
	
	/**
	 * 標準出力の内容として、指定された書式付き文字列を出力する。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see #printf(boolean, String, Object...)
	 */
	public void outPrintf(String format, Object...args) {
		printf(false, format, args);
	}
	
	/**
	 * 標準出力の内容として、指定された書式付き文字列を出力する。
	 * @param l			書式設定時に適用する <code>Locale</code>。<tt>null</tt> の場合、ローカリゼーションは適用されない。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see #printf(boolean, Locale, String, Object...)
	 */
	public void outPrintf(Locale l, String format, Object...args) {
		printf(false, l, format, args);
	}
	
	/**
	 * 標準出力の内容として、文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param str		出力する文字列
	 * @see #println(boolean, String)
	 */
	public void outPrintln(String str) {
		println(false, str);
	}
	
	/**
	 * 標準出力の内容として、指定されたオブジェクトを出力し、プラットフォーム固有の改行文字を出力する。
	 * @param str		出力するオブジェクト
	 * @see #println(boolean, Object)
	 */
	public void outPrintln(Object obj) {
		println(false, obj);
	}
	
	/**
	 * 標準出力の内容として、指定された書式付き文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see #printfln(boolean, String, Object...)
	 */
	public void outPrintfln(String format, Object...args) {
		printfln(false, format, args);
	}
	
	/**
	 * 標準出力の内容として、指定された書式付き文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param l			書式設定時に適用する <code>Locale</code>。<tt>null</tt> の場合、ローカリゼーションは適用されない。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see #printfln(boolean, Locale, String, Object...)
	 */
	public void outPrintfln(Locale l, String format, Object...args) {
		printfln(false, l, format, args);
	}
	
	/**
	 * 標準エラー出力の内容として、文字列を出力する。
	 * @param str		出力する文字列
	 * @see #print(boolean, String)
	 */
	public void errPrint(String str) {
		print(true, str);
	}
	
	/**
	 * 標準エラー出力の内容として、指定されたオブジェクトを出力する。
	 * @param str		出力するオブジェクト
	 * @see #print(boolean, Object)
	 */
	public void errPrint(Object obj) {
		print(true, obj);
	}
	
	/**
	 * 標準エラー出力の内容として、指定された書式付き文字列を出力する。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see #printf(boolean, String, Object...)
	 */
	public void errPrintf(String format, Object...args) {
		printf(true, format, args);
	}
	
	/**
	 * 標準エラー出力の内容として、指定された書式付き文字列を出力する。
	 * @param l			書式設定時に適用する <code>Locale</code>。<tt>null</tt> の場合、ローカリゼーションは適用されない。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see #printf(boolean, Locale, String, Object...)
	 */
	public void errPrintf(Locale l, String format, Object...args) {
		printf(true, l, format, args);
	}
	
	/**
	 * 標準エラー出力の内容として、文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param str		出力する文字列
	 * @see #println(boolean, String)
	 */
	public void errPrintln(String str) {
		println(true, str);
	}
	
	/**
	 * 標準エラー出力の内容として、指定されたオブジェクトを出力し、プラットフォーム固有の改行文字を出力する。
	 * @param str		出力するオブジェクト
	 * @see #println(boolean, Object)
	 */
	public void errPrintln(Object obj) {
		println(true, obj);
	}
	
	/**
	 * 標準エラー出力の内容として、指定された書式付き文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see #printfln(boolean, String, Object...)
	 */
	public void errPrintfln(String format, Object...args) {
		printfln(true, format, args);
	}
	
	/**
	 * 標準エラー出力の内容として、指定された書式付き文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param l			書式設定時に適用する <code>Locale</code>。<tt>null</tt> の場合、ローカリゼーションは適用されない。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see #printfln(boolean, Locale, String, Object...)
	 */
	public void errPrintfln(Locale l, String format, Object...args) {
		printfln(true, l, format, args);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
    
    protected void writeString(boolean isError, String str, boolean withNewLine) {
    	try {
    		synchronized (_lock) {
				ensureOpen();
				//--- write output type
				if (isError)
					_writer.write(OutputLogUtil.LOGTYPE_STDERR);
				else
					_writer.write(OutputLogUtil.LOGTYPE_STDOUT);
				//--- write field delimiter
				_writer.write(OutputLogUtil.FIELD_DELIMITER_CHAR);
				//--- write data
				_writer.write(OutputLogUtil.FIELD_ENQUOTE_CHAR);
				_writer.write(str.replaceAll("\"", "\"\""));
				if (withNewLine)
					_writer.newLine();
				_writer.write(OutputLogUtil.FIELD_ENQUOTE_CHAR);
				_writer.write(OutputLogUtil.RECORD_DELIMITER);
				//--- flush
				_writer.flush();
			}
    	}
    	catch (InterruptedIOException ex) {
    		Thread.currentThread().interrupt();
    	}
    	catch (IOException ex) {
    		_trouble = true;
    	}
    }
    
    protected void writeString(boolean isError, String str, int offset, int length, boolean withNewLine) {
    	writeString(isError, str.substring(offset, offset+length), withNewLine);
    }
    
    private void ensureOpen() throws IOException
    {
    	if (_writer == null) {
    		throw new IOException("Stream closed");
    	}
    }

    /**
     * ストリームのエラー状態を <tt>true</tt> に設定する。
     */
    protected void setError() {
    	_trouble = true;
    }

    /**
     * このストリームの内部エラー状態を解除する。
     */
    protected void clearError() {
    	_trouble = false;
    }

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
