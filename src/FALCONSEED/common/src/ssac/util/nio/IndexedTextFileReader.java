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
 * @(#)IndexedTextFileReader.java	1.19	2012/02/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IndexedTextFileReader.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import ssac.util.Validations;

/**
 * インデックスによってテキストファイルを読み込む抽象リーダークラス。
 * 
 * @version 1.19	2012/02/22
 * @since 1.16
 */
public abstract class IndexedTextFileReader implements ITextFileRecordReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final IndexedTextFileData	_fileData;
	
	private transient CharsetDecoder		_charsetDecoder;
	private transient FileChannel			_channel;
	private transient IFileRecordPointer	_recordPointer;
	private transient ByteBuffer			_byteBuffer;
	private transient CharBuffer			_charBuffer;
	
	private transient long	_lastIndex;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public IndexedTextFileReader(IndexedTextFileData fileData)
	{
		this._fileData = Validations.validNotNull(fileData, "'fileData' argument is null.");
		this._lastIndex = -1L;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isOpen() {
		return (_recordPointer!=null && _recordPointer.isOpen() && _channel!=null && _channel.isOpen());
	}
	
	public void close() throws IOException
	{
		if (_recordPointer != null) {
			final IFileRecordPointer recPointer = _recordPointer;
			this._recordPointer = null;
			recPointer.close();
		}
		
		if (_channel != null) {
			final FileChannel ch = _channel;
			this._channel = null;
			ch.close();
		}
	}

	/**
	 * このオブジェクトに関連付けられているファイルデータオブジェクトを返す。
	 * @return	<code>IndexedTextFileData</code> オブジェクト
	 */
	public IndexedTextFileData getFileData() {
		return _fileData;
	}
	
	public File getFile() {
		return getFileData().getFile();
	}
	
	public Charset getEncoding() {
		return getFileData().getEncoding();
	}
	
	public long getRecordSize() {
		return getFileData().getRecordSize();
	}
	
	public int readRecord(StringBuilder output, long index) throws IOException
	{
		if (index == getLastIndex()) {
			// 最後に読み出されたレコードキャッシュを取得
			final CharBuffer chars = getCharBuffer(false);
			if (chars != null) {
				if (output != null) {
					output.append(chars.array(), 0, chars.limit());
				}
				return chars.limit();
			}
		}

		// インデックスに対応するファイルポインタを取得
		final IFileRecordPointer recPointer = getRecordPointer(true);
		final long begin = recPointer.getBegin(index);
		final long end   = recPointer.getEnd(index);
		final int size   = (int)(end - begin);
		if (size < 1) {
			return 0;
		}

		// インデックスに対応するファイルポインタから読み込む
		final ByteBuffer bytes = getByteBuffer(true);
		final CharBuffer chars = getCharBuffer(true);
		final CharsetDecoder decoder = getCharsetDecoder(true);
		final FileChannel channel = getFileChannel();
		bytes.clear();
		bytes.limit(size);
		channel.position(begin);
		channel.read(bytes);
		bytes.flip();
		//--- バイト配列を文字に変換する
		chars.clear();
		decoder.decode(bytes, chars, true);
		chars.flip();
		if (output != null) {
			output.append(chars.array(), 0, chars.limit());
		}
		setLastIndex(index);
		return chars.limit();
	}
	
	public String readRecord(long index) throws IOException
	{
		final StringBuilder buffer = new StringBuilder();
		readRecord(buffer, index);
		return buffer.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このオブジェクトが最後にアクセスしたインデックスを返す。
	 * @return	最後にアクセスしたインデックスを返す。
	 * 			まだアクセスしていない場合は負の値を返す。
	 */
	protected long getLastIndex() {
		return _lastIndex;
	}

	/**
	 * このオブジェクトが最後にアクセスしたインデックスを設定する。
	 * @param newIndex	新しいインデックス
	 */
	protected void setLastIndex(long newIndex) {
		this._lastIndex = newIndex;
	}

	/**
	 * このオブジェクトがアクセスするファイルチャネルを生成する。
	 * @return	<code>FileChannel</code> オブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected FileChannel createFileChannel() throws IOException
	{
		final FileInputStream fis = new FileInputStream(getFileData().getFile());
		return fis.getChannel();
	}

	/**
	 * このオブジェクトがアクセスするファイルのファイルチャネルを返す。
	 * @return	<code>FileChannel</code> オブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected FileChannel getFileChannel() throws IOException
	{
		if (_channel == null) {
			this._channel = createFileChannel();
		}
		return _channel;
	}

	/*
	 * @deprecated	使用しない
	 *
	protected void setFileChannel(FileChannel newChannel) {
		this._channel = newChannel;
	}
	*/

	/**
	 * このオブジェクトの文字セットデコーダーを取得する。
	 * @param anyway	デコーダーが存在しないとき新規作成する場合は <tt>true</tt>
	 * @return	このオブジェクトに設定されている <code>CharsetDecoder</code> オブジェクトを返す。
	 * 			デコーダーが設定されていない場合は <tt>null</tt> を返す。
	 */
	protected CharsetDecoder getCharsetDecoder(boolean anyway) {
		if (_charsetDecoder == null && anyway) {
			this._charsetDecoder = getFileData().getEncoding().newDecoder()
										.onMalformedInput(CodingErrorAction.REPLACE)
										.onUnmappableCharacter(CodingErrorAction.REPLACE);
		}
		return _charsetDecoder;
	}

	/**
	 * このオブジェクトに文字セットデコーダーを設定する。
	 * @param newDecoder	設定する <code>CharsetDecoder</code> オブジェクト
	 */
	protected void setCharsetDecoder(CharsetDecoder newDecoder) {
		this._charsetDecoder = newDecoder;
	}
	
	protected ByteBuffer createByteBuffer() {
		final int capacity = getFileData().getMaxRecordByteSize();
		return createByteBuffer(capacity);
	}
	
	protected ByteBuffer createByteBuffer(int capacity) {
		final byte[] bytes = new byte[capacity];
		return ByteBuffer.wrap(bytes);
	}
	
	protected void setByteBuffer(ByteBuffer newByteBuffer) {
		this._byteBuffer = newByteBuffer;
	}
	
	protected ByteBuffer getByteBuffer(int capacity)
	{
		if (_byteBuffer == null) {
			this._byteBuffer = createByteBuffer(capacity);
		}
		else if (_byteBuffer.capacity() < capacity) {
			final ByteBuffer oldBuffer = _byteBuffer;
			ByteBuffer newBuffer = createByteBuffer(capacity);
			System.arraycopy(oldBuffer.array(), 0, newBuffer.array(), 0, oldBuffer.capacity());
			newBuffer.position(oldBuffer.position());
			newBuffer.limit(oldBuffer.limit());
			this._byteBuffer = newBuffer;
		}
		return _byteBuffer;
	}
	
	protected ByteBuffer getByteBuffer(boolean anyway) {
		if (_byteBuffer == null && anyway) {
			this._byteBuffer = createByteBuffer();
		}
		return _byteBuffer;
	}
	
	protected CharBuffer createCharBuffer() {
		final int newSize = getFileData().getMaxRecordCharSize();
		final char[] chars = new char[newSize];
		return CharBuffer.wrap(chars);
	}
	
	protected CharBuffer getCharBuffer(boolean anyway) {
		if (_charBuffer == null && anyway) {
			this._charBuffer = createCharBuffer();
		}
		return _charBuffer;
	}
	
	protected void setCharBuffer(CharBuffer newBuffer) {
		this._charBuffer = newBuffer;
	}
	
	protected IFileRecordPointer createRecordPointer() throws IOException
	{
		return getFileData().getRecordPointer();
	}
	
	protected IFileRecordPointer getRecordPointer(boolean anyway) throws IOException
	{
		if (_recordPointer == null && anyway) {
			this._recordPointer = createRecordPointer();
		}
		return _recordPointer;
	}

	/*
	 * @deprecated 使用しない
	 *
	protected void setReocrdPointer(IFileRecordPointer newPointer) {
		this._recordPointer = newPointer;
	}
	*/

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
