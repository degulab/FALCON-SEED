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
 * @(#)IndexedTextFileData.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import ssac.util.Validations;

/**
 * インデックスによるアクセスが可能なテキストファイル情報を保持する抽象クラス。
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public abstract class IndexedTextFileData extends TextFileData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** インデックスファイルのパス **/
	private final File		_indexFile;

	/** レコード数 **/
	private long	_recordSize;
	/** レコードの最大バイト数 **/
	private int	_maxRecordBytes;
	/** レコードの最大文字数 **/
	private int	_maxRecordChars;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * <code>IndexedTextFileData</code> の新しいインスタンスを生成する。
	 * @param indexFile	インデックスファイル
	 * @param file		対象ファイル
	 * @param encoding	テキストエンコーディングに適用する文字セット
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>encoding</em> がエンコード不可能な文字セットの場合
	 */
	public IndexedTextFileData(File indexFile, File file, Charset encoding) {
		super(file, encoding);
		if (indexFile == null)
			throw new NullPointerException("'indexFile' argument is null.");
		this._indexFile = indexFile;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getIndexFile() {
		return _indexFile;
	}
	
	public long getRecordSize() {
		return _recordSize;
	}
	
	public void setRecordSize(long newSize) {
		Validations.validArgument(0<=newSize, "The specified reocrd size is negative : %d", newSize);
		this._recordSize = newSize;
	}
	
	public int getMaxRecordByteSize() {
		return _maxRecordBytes;
	}
	
	public void setMaxRecordByteSize(int newSize) {
		Validations.validArgument(0<=newSize, "The specified max record byte size is negative : %d" , newSize);
		this._maxRecordBytes = newSize;
	}
	
	public int getMaxRecordCharSize() {
		return _maxRecordChars;
	}
	
	public void setMaxRecordCharSize(int newSize) {
		Validations.validArgument(0<=newSize, "The specified max record character size is negative : %d" , newSize);
		this._maxRecordChars = newSize;
	}
	
	public IFileRecordPointer getRecordPointer() throws IOException
	{
		return createRecordIndexReader();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected StoredFileRecordPointer createRecordIndexReader()
	throws IOException
	{
		return createRecordIndexReader(TextFileDataFactory.DEFAULT_BUFFER_SIZE / 8);
	}
	
	protected StoredFileRecordPointer createRecordIndexReader(int maxCacheSize)
	throws IOException
	{
		Validations.validArgument(0<=maxCacheSize, "The specified max cache size is negative : %d", maxCacheSize);
		return new StoredFileRecordPointer(getIndexFile(), maxCacheSize);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
