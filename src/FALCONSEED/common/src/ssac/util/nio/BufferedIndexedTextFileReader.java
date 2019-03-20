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
 * @(#)BufferedIndexedTextFileReader.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;

import ssac.util.ArrayHelper.SimpleIntegerArrayList;

/**
 * インデックスによってキャッシュされたテキストファイルを読み込む抽象リーダークラス。
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public abstract class BufferedIndexedTextFileReader extends IndexedTextFileReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** キャッシュとして使用するバッファの容量 **/
	private final int		_bufferCapacity;

	/** バッファにキャッシュされている先頭データのインデックス **/
	private long	_cachedBeginIndex;
	private SimpleIntegerArrayList	_beginOffsets;
	private SimpleIntegerArrayList	_endOffsets;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public BufferedIndexedTextFileReader(IndexedTextFileData fileData) {
		super(fileData);
		this._bufferCapacity = TextFileDataFactory.DEFAULT_BUFFER_SIZE;
		this.setCachedBeginIndex(-1L);
	}
	
	public BufferedIndexedTextFileReader(IndexedTextFileData fileData, int bufferCapacity) {
		super(fileData);
		this._bufferCapacity = (bufferCapacity < 0 ? 0 : bufferCapacity);
		this.setCachedBeginIndex(-1L);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getBufferCapacity() {
		return _bufferCapacity;
	}
	
	@Override
	public int readRecord(StringBuilder output, long index) throws IOException
	{
		final CharBuffer chars = readBufferedRecord(index);
		if (chars == null) {
			return 0;
		}
		if (output != null) {
			output.append(chars.array(), 0, chars.limit());
		}
		return chars.limit();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected long getCachedBeginIndex() {
		return _cachedBeginIndex;
	}
	
	protected void setCachedBeginIndex(long beginIndex) {
		this._cachedBeginIndex = beginIndex;
	}
	
	protected SimpleIntegerArrayList getBeginOffsets(boolean anyway) {
		if (_beginOffsets == null && anyway) {
			this._beginOffsets = new SimpleIntegerArrayList();
		}
		return _beginOffsets;
	}
	
	protected SimpleIntegerArrayList getEndOffsets(boolean anyway) {
		if (_endOffsets == null && anyway) {
			this._endOffsets = new SimpleIntegerArrayList();
		}
		return _endOffsets;
	}
	
	protected CharBuffer readBufferedRecord(long index) throws IOException
	{
		final long numRecords = getRecordSize();
		assert(0 <= index && index < numRecords);

		final long lastIndex = getLastIndex();
		if (index == lastIndex) {
			return getCharBuffer(false);
		}

		final SimpleIntegerArrayList beginOffsets = getBeginOffsets(true);
		final SimpleIntegerArrayList endOffsets   = getEndOffsets(true);
		final long beginIndex = getCachedBeginIndex();
		if (0 <= beginIndex && beginOffsets != null && endOffsets != null
				&& lastIndex < index && index - beginIndex < beginOffsets.size())
		{
			final ByteBuffer bytes = getByteBuffer(false);
			if (bytes != null) {
				final CharBuffer chars = getCharBuffer(true);
				final CharsetDecoder decoder = getCharsetDecoder(true);
				final int offsetIndex = (int) (index - beginIndex);
				bytes.clear();
				bytes.position(beginOffsets.get(offsetIndex));
				bytes.limit(endOffsets.get(offsetIndex));
				chars.clear();
				decoder.decode(bytes, chars, true);
				chars.flip();
				setLastIndex(index);
				return chars;
			}
		}

		beginOffsets.clear();
		endOffsets.clear();

		final IFileRecordPointer recPointer = getRecordPointer(true);
		final int bufferCapacity = getBufferCapacity();
		final long begin = recPointer.getBegin(index);
		final long end = recPointer.getEnd(index);
		final int size = (int) (end - begin);
		beginOffsets.add(0);
		endOffsets.add(size);
		int limitSize = (int) (end - begin);
		for (long newIndex = index + 1; newIndex < numRecords && limitSize < bufferCapacity; ++newIndex) {
			final long b = recPointer.getBegin(newIndex);
			final long e = recPointer.getEnd(newIndex);
			limitSize = (int) (e - begin);
			beginOffsets.add((int) (b - begin));
			endOffsets.add(limitSize);
		}
		final ByteBuffer bytes = getByteBuffer(limitSize);
		final CharBuffer chars = getCharBuffer(true);
		final CharsetDecoder decoder = getCharsetDecoder(true);
		final FileChannel channel = getFileChannel();
		bytes.clear();
		bytes.limit(limitSize);
		channel.position(begin);
		channel.read(bytes);
		bytes.flip();
		bytes.limit(size);
		chars.clear();
		decoder.decode(bytes, chars, true);
		chars.flip();
		this.setCachedBeginIndex(index);
		setLastIndex(index);
		return chars;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
