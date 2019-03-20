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
 * @(#)TextFileLineTokenizerBase.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TextFileLineTokenizerBase.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import ssac.aadl.runtime.io.Files;
import ssac.aadl.runtime.util.ArrayHelper;
import ssac.aadl.runtime.util.ArrayHelper.SimpleByteArrayList;
import ssac.aadl.runtime.util.Validations;

/**
 * テキストファイルの改行位置をトークン区切りとしてトークン分割する機能を提供する抽象クラス。
 * 
 * @version 1.17	2010/11/19
 * @since 1.16
 */
public abstract class TextFileLineTokenizerBase implements ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static public final int TT_TEXT	= 0;
	static public final int TT_EOF	= -1;
	static public final int TT_EOL	= -2;
	static public final int TT_UNKNOWN = Integer.MIN_VALUE;

	/** 標準の読み込みバッファ容量となるバイト数 **/
	static public final int DEFAULT_BUFFER_SIZE = 8192;
	/** 読み込みバッファ容量の最小バイト数 **/
	static public final int MIN_BUFFER_SIZE = 1024;
	/** 行終端のパターン **/
	static protected final String[] EOL_PATTERNS = {"\n", "\r\n", "\r"};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 読み込み対象のファイル **/
	private final File		_file;
	/** 読み込み時に適用するファイル・エンコーディング **/
	private final Charset	_encoding;

	/** 読み込みバッファの実体 **/
	private final byte[]					_byteArray;
	/** 読み込みバッファ **/
	private final ByteBuffer				_bufRead;
	/** 文字バッファ **/
	private final CharBuffer				_bufChar;
	/** トークン文字列を格納するバッファ **/
	private final StringBuilder			_stringBuffer;
	/** トークンバイトを格納するバッファ **/
	private final SimpleByteArrayList	_tokenBuffer;
	/** トークンバイトを格納するかどうかのフラグ **/
	private boolean						_storeTokenBytes = false;
	private String						_lastEolString = null;
	private SimpleByteArrayList			_lastEolBytes  = new SimpleByteArrayList();

	/** 現在の文字セットによりエンコードされた改行文字パターンのバイト配列 **/
	private final byte[][]	_eolPatterns;
	/** 改行文字パターンの最大バイト数 **/
	private final int		_maxEolPatternBytes;
	
	/** EOL をトークンとして扱うことを示すフラグ **/
	private boolean		_eolIsSignificant = false;

	/** 読み込みに使用するファイルチャネル **/
	private FileChannel	_channel;

	/** 現在の文字セットによるデコーダー **/
	private CharsetDecoder	_decoder;

	/** 読み込みバッファ先頭のファイルインデックス **/
	private long			_readBufferIndex;
	/** 読み込み済みトークン先頭のファイルインデックス **/
	private long			_tokenBeginIndex;
	/** 読み込み済みトークン終端の(次の)ファイルインデックス **/
	private long			_tokenEndIndex;
	/** 最後に読み込まれたトークン種別 **/
	private int			_lastTokenType;
	/** 次に読み込む位置の改行文字パターンインデックス **/
	private int			_nextEolPattern;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public TextFileLineTokenizerBase(File file)
	throws FileNotFoundException
	{
		this(file, (Charset)null);
	}
	
	public TextFileLineTokenizerBase(File file, String charsetName)
	throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, charsetName, DEFAULT_BUFFER_SIZE);
	}
	
	public TextFileLineTokenizerBase(File file, String charsetName, int bufferSize)
	throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, Files.getEncodingByName(charsetName), bufferSize);
	}

	public TextFileLineTokenizerBase(File file, Charset encoding)
	throws FileNotFoundException
	{
		this(file, encoding, DEFAULT_BUFFER_SIZE);
	}

	public TextFileLineTokenizerBase(File file, Charset encoding, int bufferSize)
	throws FileNotFoundException
	{
		this._file = Validations.validNotNull(file, "'File' argument is null.");
		this._encoding = (encoding==null ? Files.getDefaultEncoding() : encoding);
		int newBufferSize = Math.max(MIN_BUFFER_SIZE, bufferSize);
		this._byteArray = new byte[newBufferSize];
		this._bufRead = ByteBuffer.wrap(_byteArray);
		this._bufChar = CharBuffer.wrap(new char[newBufferSize]);
		this._stringBuffer = new StringBuilder(newBufferSize);
		this._tokenBuffer = new SimpleByteArrayList(newBufferSize);
		this._eolPatterns = newEolPatterns(_encoding);
		int maxEolBytes = 0;
		for (int i = 0; i < _eolPatterns.length; ++i) {
			maxEolBytes = Math.max(maxEolBytes, _eolPatterns[i].length);
		}
		this._maxEolPatternBytes = maxEolBytes;
		if (maxEolBytes < 1) {
			throw new IllegalStateException("Max EOL pattern bytes less than 1 : " + maxEolBytes);
		}
		open();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean getTokenBytesStored() {
		return _storeTokenBytes;
	}
	
	public void setTokenBytesStored(boolean toStore) {
		_storeTokenBytes = toStore;
	}

	/**
	 * 読み込み対象のファイルを返す。
	 */
	public File getFile() {
		return _file;
	}

	/**
	 * 読み込みに適用するテキスト・エンコーディングの文字セットを返す。
	 */
	public Charset getEncoding() {
		return _encoding;
	}
	
	/**
	 * ストリームが開いている状態の場合に <tt>true</tt> を返す。
	 */
	public boolean isOpen() {
		return (_channel != null && _channel.isOpen());
	}

	/**
	 * このストリームを閉じ、関連付けられている全てのシステムリソースを開放する。
	 * ストリームがすでに閉じている場合、このメソッドを呼び出しても何も行われない。
	 * 
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public final void close() throws IOException
	{
		if (_channel != null) {
			FileChannel fc = _channel;
			_channel = null;
			fc.close();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getLastEolString() {
		return _lastEolString;
	}
	
	protected SimpleByteArrayList getLastEolBytes() {
		return _lastEolBytes;
	}

	/**
	 * 最後に読み込まれたトークンの種別を返す。
	 * このメソッドが返す値は、次の通り。
	 * <ul>
	 * <li><code>TT_TEXT</code> は、トークンがテキストであることを示す
	 * <li><code>TT_EOL</code> は、トークンが改行文字('\r'、'\n'、'\r\n' のどれか)であることを示す
	 * <li><code>TT_EOF</code> は、入力ストリームの終端に達したことを示す
	 * </ul>
	 * @return	最後に読み込まれたトークンのトークン種別を返す。
	 */
	protected int getTokenType() {
		return _lastTokenType;
	}

	/**
	 * 最後に読み込まれたトークン先頭のファイル上の位置を返す。
	 * @return	ファイル上のトークン開始位置となるインデックス
	 */
	protected long getTokenBeginIndex() {
		return _tokenBeginIndex;
	}

	/**
	 * 最後に読み込まれたトークン終端のファイル上の位置を返す。
	 * @return	ファイル上のトークン終端位置の次の位置となるインデックス
	 */
	protected long getTokenEndIndex() {
		return _tokenEndIndex;
	}

	/**
	 * 最後に読み込まれたトークンのバイト数を返す。
	 * @return	最後に読み込まれたトークンのバイト数
	 */
	protected int getTokenByteSize() {
		return (int)(_tokenEndIndex - _tokenBeginIndex);
	}

	/**
	 * 最後に読み込まれたトークンのバイト配列を返す。
	 * @return	最後に読み込まれたトークンのバイト配列。
	 * 			トークンのバイト配列を保存していない場合は <tt>null</tt>
	 */
	protected SimpleByteArrayList getTokenBytes() {
		if (getTokenBytesStored()) {
			return _tokenBuffer;
		} else {
			return null;
		}
	}

	/**
	 * 読み込まれたトークンを返す。
	 * @return	読み込まれたトークンとなる文字列。
	 */
	protected String getTokenText() {
		return _stringBuffer.toString();
	}

	/**
	 * 行の終わり(改行文字)をトークンとして処理する場合は <tt>true</tt>、
	 * 単にトークンを区切るだけのものとして扱われる場合は <tt>false</tt> を返す。
	 */
	protected boolean isEolSignificant() {
		return _eolIsSignificant;
	}

	/**
	 * 行の終わり(改行文字)をトークンとして処理するかどうかを設定する。
	 * <em>flag</em> が <tt>true</tt> の場合、行の終わりをトークンとして処理する。
	 * 行の終わり(改行文字)が読み込まれると、nextToken メソッドは TT_EOL を返し、
	 * 現在のトークン種別をこの値に設定する。改行文字は '\r'、'\n'、'\r\n' のどれかであり、
	 * これらを行末を表す単一のトークンとして扱う。<br>
	 * <em>flag</em> が <tt>false</tt> の場合、改行文字を単にトークンを区切りだけのものとして扱う。
	 * @param flag	改行文字をトークンとして扱う場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を指定する。
	 */
	protected void setEolSignificant(boolean flag) {
		this._eolIsSignificant = flag;
	}

	/**
	 * 読み込んだトークンを格納するバッファをクリアする。
	 */
	protected void clearTokenText() {
		_stringBuffer.setLength(0);
	}

	/**
	 * このオブジェクトに設定されているバッファのバイト数を返す
	 */
	protected int getBufferSize() {
		return _bufRead.capacity();
	}

	/**
	 * このオブジェクトに設定されている読み込みバッファを返す。
	 * @return	<code>ByteBuffer</code> オブジェクト
	 */
	protected ByteBuffer getByteBuffer() {
		return _bufRead;
	}

	/**
	 * このオブジェクトに設定されている、トークン文字列を格納するバッファを返す。
	 * @return	<code>StringBuilder</code> オブジェクト
	 */
	protected StringBuilder getStringBuffer() {
		return _stringBuffer;
	}

	/**
	 * 指定された文字セットに対応するデコーダーを生成する。
	 * @param charset	デコーダーを生成する文字セット
	 * @return	生成されたデコーダー
	 */
	protected CharsetDecoder newCharsetDecoder(Charset charset) {
		CharsetDecoder newDecoder = charset.newDecoder()
										.onMalformedInput(CodingErrorAction.REPLACE)
										.onUnmappableCharacter(CodingErrorAction.REPLACE);
		return newDecoder;
	}

	/**
	 * 文字セットのデコーダーを取得する
	 * @param toCreate	デコーダーが存在しない場合に新規に作成する場合は <tt>true</tt> を指定する。
	 * @return	文字セットに対応するデコーダーを返す。存在しない場合は <tt>null</tt> を返す。
	 */
	protected CharsetDecoder getCharsetDecoder(boolean toCreate) {
		if (_decoder==null && toCreate) {
			this._decoder = newCharsetDecoder(_encoding);
		}
		return _decoder;
	}

	/**
	 * 指定された文字セットで改行文字をエンコードし、バイト配列のパターンを生成する。
	 * @param charset	エンコードに使用する文字セット
	 * @return	バイト配列のパターンを格納する配列
	 */
	static protected byte[][] newEolPatterns(Charset charset) {
		int len = EOL_PATTERNS.length;
		byte[][] bytePatterns = new byte[len][];
		for (int i = 0; i < len; ++i) {
			byte[] newPattern = null;
			try {
				newPattern = Files.encodeString(EOL_PATTERNS[i], charset);
			} catch (Exception ignoreEx) {ignoreEx=null;}
			bytePatterns[i] = (newPattern==null ? ArrayHelper.EMPTY_BYTE_ARRAY : newPattern);
		}
		return bytePatterns;
	}

	/**
	 * 指定されたファイルを開き、内部ステータスを初期化する。
	 * @throws FileNotFoundException	指定されたファイルが存在しない場合
	 * @throws IllegalStateException	ファイルがすでにオープンされている場合
	 */
	protected void open() throws FileNotFoundException
	{
		// check
		Validations.validState(!isOpen(), "Already opened the File : \"" + _file.getAbsolutePath() + "\"");
		
		// open
		FileInputStream fis = new FileInputStream(_file);
		this._channel = fis.getChannel();
		
		// initialize buffers
		this._bufRead.clear();
		this._bufChar.clear();
		this._stringBuffer.setLength(0);
		this._tokenBuffer.clear();
		
		// initialize states
		this._readBufferIndex = -1L;
		this._tokenBeginIndex = -1L;
		this._tokenEndIndex   = -1L;
		this._lastTokenType   = TT_UNKNOWN;
		this._nextEolPattern  = -1;
	}

	/**
	 * バイトバッファの内容を文字列にデコードし、その結果を指定された文字列バッファに追加する。<br>
	 * このメソッドは、{@link java.nio.ByteBuffer#position()} から {@link java.nio.ByteBuffer#limit()} までの
	 * バイトをデコードする。<br>
	 * <em>output</em> が <tt>null</tt> の場合、デコードのみが実行される。
	 * @param output	デコード結果が追加される文字列バッファ
	 * @param tbytes	デコードしたバイトが追加されるバイト配列
	 * @param buffer	デコードするバイトを保持するバイトバッファ
	 * @return	デコード結果の文字数を返す。
	 */
	protected int decodeString(StringBuilder output, SimpleByteArrayList tbytes, ByteBuffer buffer)
	{
		CharsetDecoder decoder = getCharsetDecoder(true);
		CharBuffer charbuf = _bufChar;
		charbuf.clear();
		int pos = buffer.position();
		decoder.decode(buffer, charbuf, true);
		int len = buffer.position() - pos;
		charbuf.flip();
		int nChars = charbuf.limit();
		if (output != null) {
			output.append(charbuf.array(), 0, nChars);
		}
		if (tbytes != null) {
			tbytes.append(buffer.array(), pos, len);
		}
		return nChars;
	}

	/**
	 * バイトバッファの内容を文字列にデコードし、その結果を指定された文字列バッファに追加する。<br>
	 * このメソッドは、{@link java.nio.ByteBuffer#position()} から <em>limit</em> までの
	 * バイトをデコードする。<em>limit</em> が適切な値ではない場合、このメソッドは例外をスローする。<br>
	 * <em>output</em> が <tt>null</tt> の場合、デコードのみが実行される。
	 * @param output	デコード結果が追加される文字列バッファ
	 * @param tbytes	デコードしたバイトが追加されるバイト配列
	 * @param buffer	デコードするバイトを保持するバイトバッファ
	 * @param limit		デコード範囲の一時的な終端位置を指定する
	 * @return	デコード結果の文字数を返す。
	 */
	protected int decodeString(StringBuilder output, SimpleByteArrayList tbytes, ByteBuffer buffer, int limit)
	{
		CharsetDecoder decoder = getCharsetDecoder(true);
		CharBuffer charbuf = _bufChar;
		int pos = buffer.position();
		int saveLimit = buffer.limit();
		buffer.limit(limit);
		charbuf.clear();
		decoder.decode(buffer, charbuf, true);
		int len = buffer.position() - pos;
		charbuf.flip();
		int nChars = charbuf.limit();
		if (output != null) {
			output.append(charbuf.array(), 0, nChars);
		}
		if (tbytes != null) {
			tbytes.append(buffer.array(), pos, len);
		}
		buffer.limit(saveLimit);
		return nChars;
	}

	/**
	 * 次のトークンを読み込む。
	 * このメソッドは、読み込まれたトークンを文字列バッファに追加するが、
	 * 文字列バッファの内容はクリアしない。新しく読み込まれたトークン文字列のみを取得したい場合は、
	 * このメソッドを呼び出す前に、{@link #clearTokenText()} メソッドを呼び出して文字列バッファをクリアしておく必要がある。<br>
	 * このメソッドが返す値は、次の通り。
	 * <ul>
	 * <li><code>TT_TEXT</code> は、トークンがテキストであることを示す
	 * <li><code>TT_EOL</code> は、トークンが改行文字('\r'、'\n'、'\r\n' のどれか)であることを示す
	 * <li><code>TT_EOF</code> は、入力ストリームの終端に達したことを示す
	 * </ul>
	 * @return	読み込まれたトークンのトークン種別を返す。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected int readNextToken() throws IOException
	{
		// first time
		if (_readBufferIndex < 0L) {
			// initialize for first time
			_readBufferIndex = _channel.position();
			_tokenBeginIndex = _readBufferIndex;
			_tokenEndIndex   = _readBufferIndex;
			int nRead = _channel.read(_bufRead);
			if (nRead < 0) {
				_lastTokenType = TT_EOF;
				return TT_EOF;
			}
			_bufRead.flip();
		}
		
		// Next token is EOL
		if (_nextEolPattern >= 0) {
			int len = _eolPatterns[_nextEolPattern].length;
			int pos = _bufRead.position();
			_nextEolPattern = -1;
			if (_eolIsSignificant) {
				// 改行文字をトークンとして処理する
				int eolStrPos = _stringBuffer.length();
				if (getTokenBytesStored()) {
					// トークンのバイト配列を保存
					int eolBytesPos = _tokenBuffer.size();
					decodeString(_stringBuffer, _tokenBuffer, _bufRead, pos+len);
					_lastEolBytes.clear();
					_lastEolBytes.append(_tokenBuffer, eolBytesPos, _tokenBuffer.size()-eolBytesPos);
				}
				else {
					// トークンのバイト配列は保存しない
					decodeString(_stringBuffer, null, _bufRead, pos+len);
				}
				_lastEolString = _stringBuffer.substring(eolStrPos);
				len = _bufRead.position() - pos;
				_lastTokenType = TT_EOL;
				_tokenBeginIndex = _tokenEndIndex;
				_tokenEndIndex += len;
				return _lastTokenType;
			}
			else {
				// 改行文字をスキップ
				decodeString(null, null, _bufRead, pos+len);
			}
		}
		
		// read token
		int nRead, nLimit, index;
		_tokenBeginIndex = _readBufferIndex + _bufRead.position();
		_tokenEndIndex   = _tokenBeginIndex;
		do {
			index = _bufRead.position();
			nLimit = _bufRead.limit() - _maxEolPatternBytes - 1;
			for (; index < nLimit; ) {
				// 改行文字を判定
				final int matchedPattern = matchesEolPattern(_byteArray, index, _eolPatterns);
				if (matchedPattern < 0) {
					++index;
					continue;
				}
				// 行終端発見
				_nextEolPattern = matchedPattern;
				if (getTokenBytesStored()) {
					// トークンのバイト配列を保存
					decodeString(_stringBuffer, _tokenBuffer, _bufRead, index);
				} else {
					// トークンのバイト配列は保存しない
					decodeString(_stringBuffer, null, _bufRead, index);
				}
				_lastTokenType  = TT_TEXT;
				_tokenEndIndex  = _readBufferIndex + _bufRead.position();
				return _lastTokenType;
			}
			// 行終端ではない文字はデコード
			if (index != _bufRead.position()) {
				if (getTokenBytesStored()) {
					// トークンのバイト配列を保存
					decodeString(_stringBuffer, _tokenBuffer, _bufRead, index);
				} else {
					// トークンのバイト配列は保存しない
					decodeString(_stringBuffer, null, _bufRead, index);
				}
			}
			// ファイルを読み込む
			if (_bufRead.hasRemaining()) {
				_readBufferIndex = _channel.position() - _bufRead.remaining();
				_bufRead.compact();
			} else {
				_readBufferIndex = _channel.position();
				_bufRead.clear();
			}
			nRead = _channel.read(_bufRead);
			_bufRead.flip();
		} while (nRead >= 0);
		
		// バッファの残りバイトをデコード
		if (_bufRead.hasRemaining()) {
			nLimit = _bufRead.limit();
			index = _bufRead.position();
			for (; index < nLimit; ) {
				// 改行文字を判定
				final int matchedPattern = matchesEolPattern(_byteArray, index, _eolPatterns);
				if (matchedPattern < 0) {
					++index;
					continue;
				}
				// 行終端発見
				_nextEolPattern = matchedPattern;
				if (getTokenBytesStored()) {
					// トークンのバイト配列を保存
					decodeString(_stringBuffer, _tokenBuffer, _bufRead, index);
				} else {
					// トークンのバイト配列は保存しない
					decodeString(_stringBuffer, null, _bufRead, index);
				}
				_lastTokenType  = TT_TEXT;
				_tokenEndIndex  = _readBufferIndex + _bufRead.position();
				return _lastTokenType;
			}
			// バッファの内容をすべてデコード
			if (getTokenBytesStored()) {
				// トークンのバイト配列を保存
				decodeString(_stringBuffer, _tokenBuffer, _bufRead);
			} else {
				// トークンのバイト配列は保存しない
				decodeString(_stringBuffer, null, _bufRead);
			}
			_lastTokenType = TT_TEXT;
			_tokenEndIndex = _readBufferIndex + _bufRead.position();
			return _lastTokenType;
		}
		
		// ストリーム終端に到達
		_lastTokenType = TT_EOF;
		return TT_EOF;
	}

	/**
	 * 指定されたバイト配列の指定位置から、パターンとなるバイト配列に一致するかを検証する。
	 * @param bytes		判定するバイト配列
	 * @param index		判定開始位置となるバイト配列のインデックス
	 * @param patterns	パターンとなるバイト配列を格納する配列
	 * @return	一致したパターンのインデックスを返す。どのパターンとも一致しない場合は -1 を返す。
	 */
	protected int matchesEolPattern(byte[] bytes, int index, byte[][] patterns) {
		OUTER:
		for (int i = 0, len = patterns.length; i < len; ++i) {
			byte[] pattern = patterns[i];
			int patlen = pattern.length;
			if (patlen < 1 || (bytes.length - index) < patlen) {
				continue OUTER;
			}
			for (int ii = 0; ii < patlen; ++ii) {
				if (bytes[index + ii] != pattern[ii]) {
					continue OUTER;
				}
			}
			return i;
		}
		return -1;
	}
	
	protected void clearTokenBytes() {
		_tokenBuffer.clear();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
