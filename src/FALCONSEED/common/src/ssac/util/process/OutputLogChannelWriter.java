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
 * @(#)OutputLogChannelWriter.java	3.0.0	2014/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Locale;

/**
 * 標準出力、標準エラー出力の内容を、指定されたログチャネルに書き込むクラス。
 * <p>ログファイルの構造はCSVファイルであり、第１列は種別、第２列はデータとなる。
 * 種別には、標準出力(=1)、標準エラー出力(=2)のどちらかを記述する。
 * データは改行なども含まれるため、基本的にダブルクオートでエスケープする。
 * 
 * @version 3.0.0	2014/03/24
 * @since 3.0.0
 */
public abstract class OutputLogChannelWriter implements OutputLogChannelAccessor, IOutputWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int	DEFAULT_CHARBUFFER_SIZE	= 2048;
	static protected final int	DEFAULT_BYTEBUFFER_SIZE	= 8192;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 同期用オブジェクト **/
	protected final Object		_lock;
	/** 操作対象のファイルチャネル **/
	private OutputLogChannel	_logChannel;
	/** 書き込み用エンコーダー **/
	private CharsetEncoder		_encoder;
	/** 文字バッファ **/
	private CharBuffer			_charBuffer;
	/** バイトバッファ **/
	private ByteBuffer			_byteBuffer;
	/** このプラットフォーム標準の改行文字 **/
	private String				_lineSeparator;
	/** 入出力エラーが発生した場合 **/
	private boolean				_trouble = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected OutputLogChannelWriter(Object lockObject, OutputLogChannel channel) {
		_lock = (lockObject==null ? channel : lockObject);
		_logChannel = channel;
		_encoder = _logChannel.newEncoder();
		_charBuffer = CharBuffer.wrap(new char[DEFAULT_CHARBUFFER_SIZE]);
		_byteBuffer = ByteBuffer.wrap(new byte[DEFAULT_BYTEBUFFER_SIZE]);
		_lineSeparator = System.getProperty("line.separator");
		if (_lineSeparator == null) {
			_lineSeparator = "\r\n";
		}
	}
	
	protected OutputLogChannelWriter(OutputLogChannel channel) {
		this(null, channel);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトへのアクセス時に使用するロックオブジェクトを返す。
	 * 利用者は、このロックオブジェクトにより、メソッド呼び出しよりも前に
	 * 同一のロックを獲得するために、このロックオブジェクトを使用する。
	 * @return	ロックオブジェクト(<tt>null</tt> 以外)
	 */
	public Object getLockObject() {
		return _lock;
	}

	/**
	 * このオブジェクトが出力先としているファイルの抽象パスを返す。
	 * @return	出力先ファイルの抽象パス
	 */
	public File getFile() {
		return _logChannel.getFile();
	}

	/**
	 * このオブジェクトのエンコーディングに使用されている文字セット名を返す。
	 * @return	エンコーディングの文字セット名
	 */
	public String getEncoding() {
		return _logChannel.getEncoding();
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
		if (_encoder != null) {
			flush();
		}
		return _trouble;
	}

	/**
	 * バッファにデータがあれば、その内容をチャネルに出力する。
	 */
	public void flush() {
		try {
			synchronized (_logChannel._lock) {
				ensureOpen();
				flushBytesToChannel();
			}
		}
		catch (IOException ex) {
			_trouble = true;
		}
	}

	/**
	 * このライターオブジェクトがオープンされているか判定する。
	 * @return	オープンされていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isOpened() {
		synchronized (_logChannel._lock) {
			return (_encoder != null);
		}
	}

	/**
	 * ライターをクローズする。
	 * ストリームへのアクセサが存在しない場合は、ストリームを閉じる。
	 */
	public void close() {
		synchronized (_logChannel._lock) {
			if (_encoder == null)
				return;
			_encoder.reset();
			_encoder = null;
			_charBuffer.clear();
			_charBuffer = null;
			_byteBuffer.clear();
			_byteBuffer = null;
			_logChannel.closeAccessor(this);
		}
	}

	/**
	 * 単一の文字を出力する。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param c			出力する文字
	 */
	public void print(boolean isError, char c) {
		writeChar(isError, c, false);
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
	 * 単一の文字を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param c			出力する文字
	 */
	public void println(boolean isError, char c) {
		writeChar(isError, c, true);
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
	 * 標準出力の内容として、単一の文字を出力する。
	 * @param c		出力する文字
	 * @see #print(boolean, char)
	 */
	public void outPrint(char c) {
		print(false, c);
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
	 * 標準出力の内容として、単一の文字を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param c		出力する文字
	 * @see #println(boolean, char)
	 */
	public void outPrintln(char c) {
		println(false, c);
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
	 * 標準エラー出力の内容として、単一の文字を出力する。
	 * @param c		出力する文字
	 * @see #print(boolean, char)
	 */
	public void errPrint(char c) {
		print(true, c);
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
	 * 標準エラー出力の内容として、単一の文字を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param c		出力する文字
	 * @see #println(boolean, char)
	 */
	public void errPrintln(char c) {
		println(true, c);
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
	
	protected void writeChar(boolean isError, char c, boolean withNewLine) {
		try {
			synchronized (_logChannel._lock) {
				ensureOpen();
				//--- write output type
				if (isError)
					writeToChannel(OutputLogUtil.LOGTYPE_STDERR);
				else
					writeToChannel(OutputLogUtil.LOGTYPE_STDOUT);
				//--- write field delimiter
				writeToChannel(OutputLogUtil.FIELD_DELIMITER_CHAR);
				//--- write data
				writeToChannel(OutputLogUtil.FIELD_ENQUOTE_CHAR);
				if (c == OutputLogUtil.FIELD_ENQUOTE_CHAR) {
					writeToChannel("\"\"");
				} else {
					writeToChannel(c);
				}
				if (withNewLine)
					writeNewLineToChannel();
				writeToChannel(OutputLogUtil.FIELD_ENQUOTE_CHAR);
				writeToChannel(OutputLogUtil.RECORD_DELIMITER);
				//--- flush
				flushBytesToChannel();
			}
		}
		catch (InterruptedIOException ex) {
			Thread.currentThread().interrupt();
		}
		catch (IOException ex) {
			_trouble = true;
		}
	}

	protected void writeString(boolean isError, String str, boolean withNewLine) {
		try {
			synchronized (_logChannel._lock) {
				ensureOpen();
				//--- write output type
				if (isError)
					writeToChannel(OutputLogUtil.LOGTYPE_STDERR);
				else
					writeToChannel(OutputLogUtil.LOGTYPE_STDOUT);
				//--- write field delimiter
				writeToChannel(OutputLogUtil.FIELD_DELIMITER_CHAR);
				//--- write data
				writeToChannel(OutputLogUtil.FIELD_ENQUOTE_CHAR);
				writeToChannel(str.replaceAll("\"", "\"\""));
				if (withNewLine)
					writeNewLineToChannel();
				writeToChannel(OutputLogUtil.FIELD_ENQUOTE_CHAR);
				writeToChannel(OutputLogUtil.RECORD_DELIMITER);
				//--- flush
				flushBytesToChannel();
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
	
	protected void writeNewLineToChannel() throws IOException
	{
		writeToChannel(_lineSeparator);
	}

	protected void flushBytesToChannel() throws IOException
	{

		if (_byteBuffer.position() > 0) {
			writeBytesToChannel();
		}
	}

	protected void writeBytesToChannel() throws IOException
	{
		_byteBuffer.flip();
		for (; _byteBuffer.hasRemaining(); ) {
			//--- append bytes to end
			_logChannel._channel.write(_byteBuffer, _logChannel._channel.size());
		}
		_byteBuffer.clear();
	}
	
	protected void writeToChannel(char c) throws IOException
	{
		// check buffer remaining
		if (!_charBuffer.hasRemaining())
			throw new IllegalStateException("Char buffer has no space : " + _charBuffer.remaining());
		
		// encode to bytes
		_charBuffer.put(c);
		_charBuffer.flip();
		for (; _charBuffer.hasRemaining(); ) {
			CoderResult localCoderResult = _encoder.encode(_charBuffer, _byteBuffer, false);
			if (localCoderResult.isUnderflow()) {
				break;
			}

			if (localCoderResult.isOverflow()) {
				writeBytesToChannel();
			}

			//localCoderResult.throwException();	// ?
		}
		_charBuffer.compact();
	}

	protected void writeToChannel(String str) throws IOException
	{
		// check length
		int strlen = str.length();
		if (strlen <= 0)
			return;	// nothing character
		
		// check buffer remaining
		if (!_charBuffer.hasRemaining())
			throw new IllegalStateException("Char buffer has no space : " + _charBuffer.remaining());

		// encode to bytes
		for (int strpos = 0; strpos < strlen;) {
			// CharBuffer へコピー
			int remain = _charBuffer.remaining();
			int endpos = strpos + Math.min(strlen-strpos, remain);
			_charBuffer.put(str, strpos, endpos);
			strpos = endpos;
			_charBuffer.flip();

			// バイトへ変換
			for (; _charBuffer.hasRemaining(); ) {
				CoderResult localCoderResult = _encoder.encode(_charBuffer, _byteBuffer, false);
				if (localCoderResult.isUnderflow()) {
					break;
				}

				if (localCoderResult.isOverflow()) {
					writeBytesToChannel();
				}

				//localCoderResult.throwException();	// ?
			}
			
			if (_charBuffer.hasRemaining())
				_charBuffer.compact();
			else
				_charBuffer.clear();
		}
	}

	private void ensureOpen() throws IOException
	{
		if (_encoder == null) {
			throw new IOException("Stream for Write closed");
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
