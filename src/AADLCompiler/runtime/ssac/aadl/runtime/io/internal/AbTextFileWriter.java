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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbTextFileWriter.java	1.90	2013/08/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbTextFileWriter.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import ssac.aadl.runtime.AADLMessages;
import ssac.aadl.runtime.AADLRuntimeException;

/**
 * テキストファイルに任意の文字列を出力するライターの基本クラス。
 * <p>
 * このクラスのインスタンスが生成されると、指定されたファイルをオープンし、
 * 書き込み待機状態となります。<br>
 * <b>このクラスでは、{@link #close()} メソッドが呼び出されるまでファイルは
 * 閉じられません。ファイルへの書き込みが完了したときは、
 * 必ず {@link #close()} メソッドを呼び出してファイルを閉じてください。</b>
 * 
 * @version 1.90	2013/08/07
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 * 
 * @since 1.50
 */
abstract public class AbTextFileWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このライターが読み込むファイル **/
	protected File				_targetFile;
	/** 現在のエンコーディングとなる文字セット名 **/
	protected String			_targetEncoding;
	/** <code>Writer</code> オブジェクト **/
	protected BufferedWriter	_writer;
	/** 最後に発生した例外 **/
	protected RuntimeException	_lastException;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * プラットフォーム標準のエンコーディングで指定されたファイルへ書き込む、
	 * このオブジェクトの新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを拒否する場合
	 */
	public AbTextFileWriter(File file) throws FileNotFoundException
	{
		// check
		if (file == null)
			throw new NullPointerException("'file' is null.");
		
		// create stream
		FileOutputStream fos = new FileOutputStream(file);
		
		// create reader
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(fos));
		}
		catch (RuntimeException ex) {
			dtalge.io.Files.closeStream(fos);
			fos = null;
			throw ex;
		}
		
		// setup fields
		this._targetFile     = file;
		this._targetEncoding = null;
		this._writer         = bw;
		bw  = null;
		fos = null;
	}
	
	/**
	 * 指定されたエンコーディングで指定されたファイルへ書き込む、
	 * このオブジェクトの新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @param charsetName	エンコーディングとする文字セット名。
	 * 						<tt>null</tt> の場合は、プラットフォーム標準のエンコーディングとなる。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを拒否する場合
	 */
	public AbTextFileWriter(File file, String charsetName)
	throws FileNotFoundException, UnsupportedEncodingException
	{
		// check
		if (file == null)
			throw new NullPointerException("'file' is null.");
		if (charsetName == null)
			throw new NullPointerException("'charsetName' is null.");
		
		// create stream
		FileOutputStream fos = new FileOutputStream(file);
		
		// create reader
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(fos, charsetName));
		}
		catch (UnsupportedEncodingException ex) {
			dtalge.io.Files.closeStream(fos);
			fos = null;
			throw ex;
		}
		catch (RuntimeException ex) {
			dtalge.io.Files.closeStream(fos);
			fos = null;
			throw ex;
		}
		
		// setup fields
		this._targetFile     = file;
		this._targetEncoding = charsetName;
		this._writer         = bw;
		bw  = null;
		fos = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 書き込み対象のファイルを返します。
	 * @return	書き込み対象のファイル
	 */
	public File getFile() {
		return _targetFile;
	}

	/**
	 * 書き込み時のエンコーディングとなる文字セット名を返します。
	 * エンコーディングが指定されていない場合は <tt>null</tt> を返します。
	 * @return	エンコーディングの文字セット名を返す。
	 * 			エンコーディングが指定されていない場合は <tt>null</tt> を返す。
	 */
	public String getEncoding() {
		return _targetEncoding;
	}

	/**
	 * ファイルが閉じられている場合に <tt>true</tt> を返します。
	 * @return	ファイルが閉じられている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isOpen() {
		return (_writer != null);
	}

	/**
	 * このライターが最後にスローした <code>Exception</code> を返します。この種の例外が存在しない場合、このメソッドは <tt>null</tt> を返します。
	 * @return	このライターが最後にスローした例外
	 */
	public RuntimeException lastException() {
		return _lastException;
	}

	/**
	 * ファイルを閉じます。
	 * すでに閉じられている場合は、何もしません。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常にファイルを閉じた場合、もしくは既に閉じられている場合は <tt>true</tt> を返す。
	 * 			閉じられなかった場合は <tt>false</tt> を返す。
	 */
	public boolean close() {
		boolean result = true;
		if (_writer != null) {
			try {
				_writer.close();
			} catch (Exception ex) {
				_lastException = new AADLRuntimeException(AADLMessages.formatFailedCloseMessage(_targetFile), ex);
				result = false;
			}
			_writer = null;
		}
		return result;
	}

	/**
	 * バッファの内容をファイルへ書き込みます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean flush() {
		boolean result = true;
		try {
			ensureOpen();
			_writer.flush();
		}
		catch (Exception ex) {
			_lastException = new AADLRuntimeException(AADLMessages.formatFailedWriteMessage(_targetFile), ex);
			result = false;
		}
		return result;
	}

	/**
	 * レコード区切り文字を書き込みます。通常、このメソッドが出力するレコード区切り
	 * 文字は Java 標準の改行文字であり、プラットフォーム標準の改行文字が出力されます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean newRecord() {
		return writeRecordSeparator();
	}
	
	

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このライターのストリームが開いているかを確認する。
	 * ストリームが閉じている場合は、例外をスローする。
	 * @throws IOException	ストリームが閉じている場合
	 */
	protected void ensureOpen() throws IOException {
		if (_writer == null) {
			throw new IOException("This stream closed.");
		}
	}
	
	/**
	 * 指定された文字列を書き込みます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected boolean write(String str) {
		boolean result = true;
		try {
			ensureOpen();
			_writer.write(str);
		}
		catch (Exception ex) {
			_lastException = new AADLRuntimeException(AADLMessages.formatFailedWriteMessage(_targetFile), ex);
			result = false;
		}
		return result;
	}
	
	/**
	 * レコード区切り文字を書き込みます。通常、このメソッドが出力するレコード区切り
	 * 文字は Java 標準の改行文字であり、プラットフォーム標準の改行文字が出力されます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean writeRecordSeparator() {
		boolean result = true;
		try {
			ensureOpen();
			_writer.newLine();
		}
		catch (Exception ex) {
			_lastException = new AADLRuntimeException(AADLMessages.formatFailedWriteMessage(_targetFile), ex);
			result = false;
		}
		return result;
	}
}
