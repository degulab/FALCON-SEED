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
 * @(#)AbTextFileReader.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ssac.aadl.runtime.AADLMessages;
import ssac.aadl.runtime.AADLRuntimeException;

/**
 * テキストファイルから 1 レコードずつ読み込むリーダーの基本クラス。
 * <p>このクラスは、自身がイテレータであり、その反復子を利用して
 * テキストファイルから 1 レコードずつ取得します。1 レコードとする単位は、
 * このクラスから派生されたクラスの実装によります。
 * <p>
 * このクラスのインスタンスが生成されると、指定されたファイルをオープンし、
 * 読み込み待機状態となります。反復子がファイルの最後まで到達するか、
 * 読み込みエラーが発生した場合、このファイルは自動的に閉じられます。
 * ファイルが最後まで読み込まれない状態で処理を中断した場合は、
 * {@link #close()} メソッドを呼び出してファイルを閉じてください。
 * 
 * @version 1.50	2010/09/29
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
abstract public class AbTextFileReader<T> implements Iterable<T>, Iterator<T>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このリーダーが読み込むファイル **/
	protected File				_targetFile;
	/** 現在のエンコーディングとなる文字セット名 **/
	protected String			_targetEncoding;
	/** <code>Reader</code> オブジェクト **/
	protected BufferedReader	_reader;
	/** 読み込み済みの次のレコード **/
	protected T				_nextRecord;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * プラットフォーム標準のエンコーディングで指定されたファイルを読み込む、
	 * <code>AbTextFileReader</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public AbTextFileReader(File file)
	throws FileNotFoundException
	{
		// check
		if (file == null)
			throw new NullPointerException("'file' is null.");
		
		// create stream
		FileInputStream fis = new FileInputStream(file);
		
		// create reader
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(fis));
		}
		catch (RuntimeException ex) {
			dtalge.io.Files.closeStream(fis);
			fis = null;
			throw ex;
		}
		
		// setup fields
		this._targetFile     = file;
		this._targetEncoding = null;
		this._reader         = br;
		br  = null;
		fis = null;
	}
	
	/**
	 * 指定されたエンコーディングで指定されたファイルを読み込む、
	 * <code>AbTextFileReader</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @param charsetName	エンコーディングとする文字セット名。
	 * 						<tt>null</tt> の場合は、プラットフォーム標準のエンコーディングとなる。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public AbTextFileReader(File file, String charsetName)
	throws FileNotFoundException, UnsupportedEncodingException
	{
		// check
		if (file == null)
			throw new NullPointerException("'file' is null.");
		if (charsetName == null)
			throw new NullPointerException("'charsetName' is null.");
		
		// create stream
		FileInputStream fis = new FileInputStream(file);
		
		// create reader
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(fis, charsetName));
		}
		catch (UnsupportedEncodingException ex) {
			dtalge.io.Files.closeStream(fis);
			fis = null;
			throw ex;
		}
		catch (RuntimeException ex) {
			dtalge.io.Files.closeStream(fis);
			fis = null;
			throw ex;
		}
		
		// setup fields
		this._targetFile     = file;
		this._targetEncoding = charsetName;
		this._reader         = br;
		br  = null;
		fis = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 読み込み対象のファイルを返します。
	 * @return	読み込み対象のファイル
	 */
	public File getFile() {
		return _targetFile;
	}

	/**
	 * 読み込み時のエンコーディングとなる文字セット名を返します。
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
		return (_reader != null);
	}

	/**
	 * ファイルを閉じます。
	 * すでに閉じられている場合は、何もしません。
	 */
	public void close() {
		if (_reader != null) {
			dtalge.io.Files.closeStream(_reader);
			_reader = null;
		}
	}

	//------------------------------------------------------------
	// Implement Iterable interfaces
	//------------------------------------------------------------

	/**
	 * ファイルから行を繰り返し取得するための反復子を返します。
	 * このメソッドが返す反復子は <code>remove</code> をサポートしません。
	 * @return	反復子
	 */
	public Iterator<T> iterator() {
		return this;
	}

	//------------------------------------------------------------
	// Implement Iterator interfaces
	//------------------------------------------------------------

	/**
	 * 次のレコードが読み込み可能である場合に <tt>true</tt> を返します。
	 * 読み込みエラーが発生した場合は例外をスローします。
	 * @throws AADLRuntimeException	読み込みエラーが発生した場合にスローされます。
	 * 									この例外がスローされた要因は、{@link AADLRuntimeException#getCause()} で
	 * 									取得できます。
	 */
	public boolean hasNext() {
		return ensureNextRecord();
	}

	/**
	 * 次のレコードを読み込み、読み込まれたレコードを返します。
	 * このメソッドが返すレコードにはレコード区切り文字は含まれません。
	 * 読み込みエラーが発生した場合は例外をスローします。
	 * @return	レコード区切り文字を含まない 1 レコード分のオブジェクト
	 * @throws NoSuchElementException	読み込むレコードが存在しない場合
	 * @throws AADLRuntimeException	読み込みエラーが発生した場合にスローされます。
	 * 									この例外がスローされた要因は、{@link AADLRuntimeException#getCause()} で
	 * 									取得できます。
	 */
	public T next() {
		if (ensureNextRecord()) {
			return popNextRecord();
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * このメソッドはサポートされません。
	 * @throws UnsupportedOperationException	常にスローされる
	 */
	public void remove() {
		throw new UnsupportedOperationException("Unsupported \"remove\" operation!");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 読み込み済みの次のレコードを取得し、読み込み済みレコードをクリアします。
	 * @return	読み込み済みレコードが存在する場合はそのオブジェクトを返す。
	 * 			存在しない場合は <tt>null</tt> を返す。
	 */
	protected T popNextRecord() {
		T rec = _nextRecord;
		_nextRecord = null;
		return rec;
	}

	/**
	 * 新しいレコードを読み込み、読み込み済みレコードを更新します。
	 * このメソッドは、読み込み済みレコードの有無に関わらず、
	 * 新しいレコードを読み込みます。
	 * @return	新しいレコードが読み込まれた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException	読み込みエラーが発生した場合
	 */
	abstract protected boolean readNextRecord() throws IOException;

	/**
	 * 次のレコードが読み込まれていない場合は、次のレコードを読み込む。
	 * @return	次のレコードが存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws AADLRuntimeException	読み込みエラーが発生した場合にスローされます。
	 * 									この例外がスローされた要因は、{@link AADLRuntimeException#getCause()} で
	 * 									取得できます。
	 */
	protected boolean ensureNextRecord() throws AADLRuntimeException
	{
		if (_nextRecord != null) {
			return true;
		}
		
		if (_reader == null) {
			return false;
		}

		try {
			if (!readNextRecord()) {
				close();
				return false;
			} else {
				return true;
			}
		}
		catch (IOException ex) {
			close();
			throw new AADLRuntimeException(AADLMessages.formatFailedReadMessage(_targetFile), ex);
		}
		catch (Throwable ex) {
			close();
			throw new AADLRuntimeException(ex);
		}
	}
}
