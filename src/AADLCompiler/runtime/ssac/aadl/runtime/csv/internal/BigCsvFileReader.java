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
 * @(#)BigCsvFileReader.java	1.90	2013/08/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.csv.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ssac.aadl.runtime.AADLMessages;
import ssac.aadl.runtime.AADLRuntimeException;
import ssac.aadl.runtime.io.internal.ITokenPosition;
import ssac.aadl.runtime.io.internal.TextFileLineTokenizer;

public class BigCsvFileReader implements Iterable<List<String>>, Iterator<List<String>>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 現在のエンコーディングとなる文字セット名 **/
	protected String			_targetEncoding;
	/** CSV ファイルのトークン分割 **/
	protected CsvFileTokenizer	_csvTokenizer;

	/** 現在の読み込み済みレコード数 **/
	protected long			_curRecCount;
	/** 現在の読み込み済み最大フィールド数 **/
	protected int			_curMaxFields;
	/** 現在のレコードの開始位置(初期値=-1) **/
	protected long			_curRecBeginPos = (-1L);
	/** 現在のレコードの終了位置(初期値=-1) **/
	protected long			_curRecEndPos   = (-1L);
	/** 次のレコードの開始位置(初期値=-1) **/
	protected long			_nextRecBeginPos = (-1L);
	/** 次のレコードの終了位置(初期値=-1) **/
	protected long			_nextRecEndPos   = (-1L);
	/** 読み込み済みの次のレコード **/
	protected List<String>	_nextRecord;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * プラットフォーム標準のエンコーディングと標準のフォーマットで、指定されたファイルを読み込む、
	 * <code>BigCsvFileReader</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public BigCsvFileReader(File file) throws FileNotFoundException
	{
		this(file, (CsvFormat)null, TextFileLineTokenizer.DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * プラットフォーム標準のエンコーディングと標準のフォーマットで、指定されたファイルを読み込む、
	 * <code>BigCsvFileReader</code> の新しいインスタンスを生成します。
	 * このメソッドでは、<em>bufferSize</em> に指定されたバイト数で読み込みバッファを確保します。
	 * <em>bufferSize</em> が 1024 未満の場合、1024 バイトのバッファを確保します。
	 * @param file	読み込むファイル
	 * @param bufferSize	読み込みバッファのサイズ
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public BigCsvFileReader(File file, int bufferSize) throws FileNotFoundException
	{
		this(file, (CsvFormat)null, bufferSize);
	}
	
	/**
	 * プラットフォーム標準のエンコーディングと指定されたフォーマットで、指定されたファイルを読み込む、
	 * <code>BigCsvFileReader</code> の新しいインスタンスを生成します。
	 * <em>format</em> が <tt>null</tt> の場合、標準のフォーマットで読み込みます。
	 * @param file		読み込むファイル
	 * @param format	CSV のフォーマット
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public BigCsvFileReader(File file, CsvFormat format) throws FileNotFoundException
	{
		this(file, format, TextFileLineTokenizer.DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * 指定されたエンコーディングと標準のフォーマットで、指定されたファイルを読み込む、
	 * <code>BigCsvFileReader</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @param encoding	エンコーディングとする文字セット名
	 * @throws NullPointerException	<em>file</em> もしくは <em>encoding</em> が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public BigCsvFileReader(File file, String encoding) throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, null, encoding, TextFileLineTokenizer.DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * 指定されたエンコーディングと標準のフォーマットで、指定されたファイルを読み込む、
	 * <code>BigCsvFileReader</code> の新しいインスタンスを生成します。
	 * このメソッドでは、<em>bufferSize</em> に指定されたバイト数で読み込みバッファを確保します。
	 * <em>bufferSize</em> が 1024 未満の場合、1024 バイトのバッファを確保します。
	 * @param file	読み込むファイル
	 * @param encoding	エンコーディングとする文字セット名
	 * @param bufferSize	読み込みバッファのサイズ
	 * @throws NullPointerException	<em>file</em> もしくは <em>encoding</em> が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public BigCsvFileReader(File file, String encoding, int bufferSize) throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, null, encoding, bufferSize);
	}
	
	/**
	 * 指定されたエンコーディングと指定されたフォーマットで、指定されたファイルを読み込む、
	 * <code>BigCsvFileReader</code> の新しいインスタンスを生成します。
	 * <em>format</em> が <tt>null</tt> の場合、標準のフォーマットで読み込みます。
	 * @param file	読み込むファイル
	 * @param format	CSV のフォーマット
	 * @param encoding	エンコーディングとする文字セット名
	 * @throws NullPointerException	<em>file</em> もしくは <em>encoding</em> が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public BigCsvFileReader(File file, CsvFormat format, String encoding) throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, format, encoding, TextFileLineTokenizer.DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * プラットフォーム標準のエンコーディングと指定されたフォーマットで、指定されたファイルを読み込む、
	 * <code>BigCsvFileReader</code> の新しいインスタンスを生成します。
	 * <em>format</em> が <tt>null</tt> の場合、標準のフォーマットで読み込みます。
	 * このメソッドでは、<em>bufferSize</em> に指定されたバイト数で読み込みバッファを確保します。
	 * <em>bufferSize</em> が 1024 未満の場合、1024 バイトのバッファを確保します。
	 * @param file		読み込むファイル
	 * @param format	CSV のフォーマット
	 * @param bufferSize	読み込みバッファのサイズ
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public BigCsvFileReader(File file, CsvFormat format, int bufferSize) throws FileNotFoundException
	{
		// check
		if (file == null)
			throw new NullPointerException("The specified File object is null.");
		
		// create tokenizer
		_targetEncoding = null;
		_csvTokenizer = new CsvFileTokenizer(file, bufferSize);
		if (format != null) {
			_csvTokenizer.setCsvFormat(format);
		}
	}
	
	/**
	 * 指定されたエンコーディングと指定されたフォーマットで、指定されたファイルを読み込む、
	 * <code>BigCsvFileReader</code> の新しいインスタンスを生成します。
	 * <em>format</em> が <tt>null</tt> の場合、標準のフォーマットで読み込みます。
	 * このメソッドでは、<em>bufferSize</em> に指定されたバイト数で読み込みバッファを確保します。
	 * <em>bufferSize</em> が 1024 未満の場合、1024 バイトのバッファを確保します。
	 * @param file	読み込むファイル
	 * @param format	CSV のフォーマット
	 * @param encoding	エンコーディングとする文字セット名
	 * @param bufferSize	読み込みバッファのサイズ
	 * @throws NullPointerException	<em>file</em> もしくは <em>encoding</em> が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public BigCsvFileReader(File file, CsvFormat format, String encoding, int bufferSize) throws FileNotFoundException, UnsupportedEncodingException
	{
		// check
		if (file == null)
			throw new NullPointerException("The specified File object is null.");
		if (encoding == null)
			throw new NullPointerException("The specified 'encoding' argument is null.");
		
		// create tokenizer
		_targetEncoding = encoding;
		_csvTokenizer = new CsvFileTokenizer(file, encoding);
		if (format != null) {
			_csvTokenizer.setCsvFormat(format);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 読み込み対象のファイルを返します。
	 * @return	読み込み対象のファイル
	 */
	public File getFile() {
		return _csvTokenizer.getFile();
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
	 * 現在の CSV フォーマットを返す。
	 * @return <code>CsvFormat</code> オブジェクト
	 */
	public CsvFormat getCsvFormat() {
		return _csvTokenizer.getCsvFormat();
	}

	/**
	 * 新しい CSV フォーマットを設定する。
	 * @param newFormat	<code>CsvFormat</code> オブジェクト
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean setCsvFormat(final CsvFormat newFormat) {
		return _csvTokenizer.setCsvFormat(newFormat);
	}

	/**
	 * 最後に読み込まれた CSV レコード先頭のファイル上の位置を返す。
	 * @return	ファイル上の CSV レコード開始位置となるインデックス、読み込まれていない場合は (-1)
	 */
	public long getRecordBeginIndex() {
		return _curRecBeginPos;
	}

	/**
	 * 最後に読み込まれた CSV レコード終端のファイル上の位置を返す。
	 * @return	ファイル上の CSV レコード終端位置の次の位置となるインデックス、読み込まれていない場合は (-1)
	 */
	public long getRecordEndIndex() {
		return _curRecEndPos;
	}

	/**
	 * 最後に読み込まれた CSV レコードのバイト数を返す。
	 * @return	最後に読み込まれた CSV レコードのバイト数
	 */
	public long getRecordByteSize() {
		return (_curRecEndPos - _curRecBeginPos);
	}

	/**
	 * 読み込まれた CSV レコード数を返す。
	 * @return	読み込まれた CSV レコード数
	 */
	public long getRecordCount() {
		return _curRecCount;
	}

	/**
	 * CSV レコードの最大フィールド数を返す。
	 * @return	読み込まれた CSV レコードの最大フィールド数
	 */
	public int getMaxFieldCount() {
		return _curMaxFields;
	}

	/**
	 * ストリームが開いている状態の場合に <tt>true</tt> を返す。
	 */
	public boolean isOpen() {
		return _csvTokenizer.isOpen();
	}

	/**
	 * このストリームを閉じ、関連付けられている全てのシステムリソースを開放する。
	 * ストリームがすでに閉じている場合、このメソッドを呼び出しても何も行われない。
	 */
	public void close()
	{
		try {
			_csvTokenizer.close();
		} catch (Throwable ignoreEx) {}
	}

	/**
	 * 次のレコードの読み込み位置を保持する、新しい位置オブジェクトを返す。
	 */
	public ITokenPosition getNextReadPosition() {
		return _csvTokenizer.getNextReadPosition();
	}

	/**
	 * 最後に読み込まれたレコードの読み込み位置を保持する、新しい位置オブジェクトを返す。
	 */
	public ITokenPosition getCurrentTokenPosition() {
		return _csvTokenizer.getCurrentTokenPosition();
	}

	/**
	 * マークされたレコード読み込み位置の、新しいインスタンスを返す。
	 * マークされていない場合は <tt>null</tt> を返す。
	 * @return	マークされた位置、マークされていない場合は <tt>null</tt>
	 */
	public ITokenPosition getMarkedReadPosition() {
		return _csvTokenizer.getMarkedReadPosition();
	}

	/**
	 * 次のレコード先頭読み込み位置を保存する。
	 */
	public void markNextReadPosition() {
		_csvTokenizer.markNextReadPosition();
	}

	/**
	 * 最後に読み込まれたトークンの先頭読み込み位置を保存する。
	 */
	public void markCurrentTokenPosition() {
		_csvTokenizer.markCurrentTokenPosition();
	}

	/**
	 * 保存された読み込み位置をクリアする。
	 */
	public void clearMarkedReadPosition() {
		_csvTokenizer.clearMarkedReadPosition();
	}

	/**
	 * 次のレコード先頭読み込み位置を、保存された読み込み位置に設定する。
	 * 読み込み位置が保存されていない場合は、読み込み位置を変更せず、現在の読み込み位置を返す。
	 * @throws RuntimeException	現在のチャネルがクローズしている場合、もしくは、その他の入出力エラーが発生した場合
	 */
	public void seekToMarkedReadPosition()
	{
		try {
			_csvTokenizer.seekToMarkedReadPosition();
		}
		catch (RuntimeException ex) {
			close();
			throw ex;
		}
		catch (IOException ex) {
			close();
			throw new AADLRuntimeException(AADLMessages.formatFailedReadMessage(getFile()), ex);
		}
		catch (Throwable ex) {
			close();
			throw new AADLRuntimeException(ex);
		}
	}

	/**
	 * 次のレコード先頭読み込み位置を、指定された位置に設定する。
	 * この操作では、現在の読み込み状態を破棄し、新しい読み込み位置を設定する。
	 * @param position	新しい読み込み開始位置とする位置オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	引数がこのクラスの位置情報オブジェクトではない場合
	 * @throws IllegalArgumentException	指定された位置が適切な位置情報ではない場合
	 * @throws RuntimeException	現在のチャネルがクローズしている場合、もしくは、その他の入出力エラーが発生した場合
     */
	public void seekToReadPosition(ITokenPosition position)
	{
		try {
			_csvTokenizer.seekToReadPosition(position);
		}
		catch (RuntimeException ex) {
			close();
			throw ex;
		}
		catch (IOException ex) {
			close();
			throw new AADLRuntimeException(AADLMessages.formatFailedReadMessage(getFile()), ex);
		}
		catch (Throwable ex) {
			close();
			throw new AADLRuntimeException(ex);
		}
	}

	//------------------------------------------------------------
	// Implements java.util.Iterable interfaces
	//------------------------------------------------------------

	/**
	 * ファイルから行を繰り返し取得するための反復子を返します。
	 * このメソッドが返す反復子は <code>remove</code> をサポートしません。
	 * @return	反復子
	 */
	public Iterator<List<String>> iterator() {
		return this;
	}

	//------------------------------------------------------------
	// Implements java.util.Iterator interfaces
	//------------------------------------------------------------

	/**
	 * 次のレコードが読み込み可能である場合に <tt>true</tt> を返します。
	 * 読み込みエラーが発生した場合は例外をスローします。
	 * @throws AADLRuntimeException	読み込みエラーが発生した場合にスローされます。
	 * 									この例外がスローされた要因は、{@link AADLRuntimeException#getCause()} で
	 * 									取得できます。
	 */
	public boolean hasNext() {
		if (_csvTokenizer.isOpen()) {
			try {
				return _csvTokenizer.hasNext();
			}
			catch (IOException ex) {
				close();
				throw new AADLRuntimeException(AADLMessages.formatFailedReadMessage(getFile()), ex);
			}
			catch (Throwable ex) {
				close();
				throw new AADLRuntimeException(ex);
			}
		}
		else {
			return false;
		}
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
	public List<String> next() {
		if (_csvTokenizer.isOpen()) {
			List<String> rec;
			try {
				rec = _csvTokenizer.nextRecord();
			}
			catch (IOException ex) {
				close();
				throw new AADLRuntimeException(AADLMessages.formatFailedReadMessage(getFile()), ex);
			}
			catch (Throwable ex) {
				close();
				throw new AADLRuntimeException(ex);
			}
			if (rec != null) {
				return rec;
			} else {
				close();
				throw new NoSuchElementException();
			}
		}
		else {
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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
