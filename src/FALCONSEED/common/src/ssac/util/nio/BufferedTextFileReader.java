/*
 * @(#)BufferedTextFileReader.java	3.4.0	2020/03/13
 * @(#)TextFileLineTokenizerBase.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import ssac.util.Validations;
import ssac.util.io.Files;

/**
 * テキストファイルの読み込みバイト位置を取得可能なバッファリングリーダー。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class BufferedTextFileReader extends Reader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 標準の読み込みバッファ容量となるバイト数 **/
	static public final int DEFAULT_BUFFER_SIZE = 16384;	// 8192
	/** 読み込みバッファ容量の最小バイト数 **/
	static public final int MIN_BUFFER_SIZE = 1024;
	
	static protected final int	INVALIDATED	= -2;
	static protected final int	UNMARKED	= -1;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 読み込み対象のファイル(同期オブジェクトに利用) **/
	protected final File		_file;
	/** 読み込み時に適用するファイル・エンコーディング **/
	protected final Charset	_encoding;

	/** 読み込みバッファの実体 **/
	private byte[]			_bufByteArray;
	/** 読み込みバッファ **/
	private ByteBuffer		_bufRead;
	/** 読み込みバッファが読み込み用であることを示すフラグ **/
	private boolean			_bufReadable;
	/** 文字バッファの実体 **/
	private char[]			_bufCharArray;
	/** 文字バッファ **/
	private CharBuffer		_bufChar;
	/** 現在の文字バッファ直前の未消費文字列を保持する未消費文字バッファ、マーク処理に使用、マークが一度も実行されていない場合は <tt>null</tt> **/
	private StringBuilder	_bufUnconsumedChars;

	/** ファイルチャネルと連動するストリーム **/
	private FileInputStream	_stream;
	/** 読み込みに使用するファイルチャネル **/
	private FileChannel	_channel;
	/** 読み込みチャネルが終端に到達していることを示すフラグ **/
	private boolean			_eof;

	/** 現在の文字セットによるデコーダー **/
	private CharsetDecoder	_decoder;

	/** 実際にファイルから読み込まれたバイト数 **/
	private long		_actualReadBytes;
	/** 読込バッファ先頭のファイル上の位置 **/
	private long		_readBufHeadFilePos;
	/** 文字バッファ先頭のファイル上の位置 **/
	private long		_charBufHeadFilePos;
	/** 文字バッファに読み込まれたファイル上のバイト数(文字バッファ内の文字数に対応) **/
	private long		_charBufByteLength;
	/** 文字バッファに読み込まれた文字数、文字バッファが空なら 0 **/
	private int			_charBufCharLength;
	
	/** 次に読み込む文字バッファ位置 **/
	private int			_nextCharBufPos;
	/** 次に読み込む文字がスキップ対象の LF であることを示すフラグ **/
	private boolean		_nextSkipLF		= false;
	
	/** マークされた文字バッファ位置、マークが無効なら負の値 **/
	private int			_markedCharBufPos;
	/** マークされた文字がスキップ対象の LF であることを示すフラグ **/
	private boolean		_markedSkipLF	= false;
	/** マークを維持して読み込み可能な文字数上限 **/
	private int			_readAheadLimit = 0;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public BufferedTextFileReader(File file)
	throws FileNotFoundException
	{
		this(file, (Charset)null);
	}
	
	public BufferedTextFileReader(File file, String charsetName)
	throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, charsetName, DEFAULT_BUFFER_SIZE);
	}
	
	public BufferedTextFileReader(File file, String charsetName, int bufferSize)
	throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, Files.getEncodingByName(charsetName), bufferSize);
	}

	public BufferedTextFileReader(File file, Charset encoding)
	throws FileNotFoundException
	{
		this(file, encoding, DEFAULT_BUFFER_SIZE);
	}

	public BufferedTextFileReader(File file, Charset encoding, int bufferSize)
	throws FileNotFoundException
	{
		super(Validations.validNotNull(file, "'File' argument is null."));	// null check
		_file = file;	// synchronized object
		_encoding = (encoding==null ? Files.getDefaultEncoding() : encoding);
		int newBufferSize = Math.max(MIN_BUFFER_SIZE, bufferSize);
		_bufByteArray = new byte[newBufferSize];
		_bufRead = ByteBuffer.wrap(_bufByteArray);
		_bufCharArray = new char[newBufferSize];
		_bufChar = CharBuffer.wrap(_bufCharArray);
		_bufUnconsumedChars = null;	// 初期状態では null
		_decoder = _encoding.newDecoder()
								.onMalformedInput(CodingErrorAction.REPLACE)
								.onUnmappableCharacter(CodingErrorAction.REPLACE);
		_decoder.reset();
		open();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 現在のファイル読み込みバッファ先頭のファイル位置
	 * @return	ファイル位置
	 */
	public long getByteBufferHeadFilePosition() {
		synchronized (lock) {
			return _readBufHeadFilePos;
		}
	}
	
	/**
	 * 実際にファイルから読み込まれたバイト数
	 * @return
	 */
	public long getActualReadBytesFromFile() {
		synchronized (lock) {
			return _actualReadBytes;
		}
	}
	
	/**
	 * 実際のファイル読み込み位置
	 * @return	ファイル読み込み位置、取得できなかった場合は (-1);
	 */
	public long getActualFilePosition() {
		synchronized (lock) {
			if (_channel != null && _channel.isOpen()) {
				try {
					return _channel.position();
				}
				catch (Throwable ex) {
					return (-1L);
				}
			}
			else {
				return (-1L);
			}
		}
	}
	
	/**
	 * 読み込み対象ファイルのバイト数
	 * @return	バイト数
	 */
	public long getFileBytes() {
		return _file.length();
	}
	
	/**
	 * 現在の読み込み位置に対する、読み込み済みバイト数を取得する。
	 * このメソッドが返す値は、文字セットによって近似値となる。
	 * @return	現在の読み込み位置に対する読み込み済みバイト数
	 */
	public long getReadBytes() {
		synchronized (lock) {
			// 文字バッファの位置から計算
			return (_charBufHeadFilePos + (long)((double)_nextCharBufPos / (double)_charBufCharLength * (double)_charBufByteLength));
		}
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
		synchronized (lock) {
			return (_channel != null && _channel.isOpen());
		}
	}

	/**
	 * このストリームを閉じ、関連付けられている全てのシステムリソースを開放する。
	 * ストリームがすでに閉じている場合、このメソッドを呼び出しても何も行われない。
	 * 
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void close() throws IOException
	{
		synchronized (lock) {
			if (_channel != null) {
				try {
					_channel.close();
				}
				finally {
					_channel = null;
					_stream  = null;
					_bufChar = null;
					_bufRead = null;
					_bufByteArray = null;
					_bufCharArray = null;
					_bufUnconsumedChars = null;
					_decoder.reset();
					_decoder = null;
				}
			}
		}
	}

	//------------------------------------------------------------
	// Implement Reader interfaces
	//------------------------------------------------------------

	/**
	 * 単一の文字を読み込む。
	 * @return	<code>0 - 65535</code> (<code>0x00-0xffff</code>) の範囲の整数としての、読み込まれた文字。
	 * 			ストリームの終わりに達した場合は (-1)
	 * @throws IOException	入出力エラーが発生した場合、またはストリームがすでに閉じている場合
	 */
	@Override
	public int read() throws IOException
	{
		synchronized (lock) {
			ensureOpened();
//			// 未消費バッファからの読み込み
//			if (_nextUnconsumedCharBufPos >= 0) {
//				for (;;) {
//					if (_nextUnconsumedCharBufPos >= _bufUnconsumedChars.length()) {
//						// 未消費文字バッファはすべて消費
//						_nextUnconsumedCharBufPos = (-1);
//						_nextCharBufPos = 0;	// 文字バッファの先頭から
//						break;
//					}
//					//--- skip LF
//					if (_nextSkipLF) {
//						_nextSkipLF = false;
//						if (_bufUnconsumedChars.charAt(_nextUnconsumedCharBufPos) == '\n') {
//							++_nextUnconsumedCharBufPos;
//							continue;
//						}
//					}
//					//--- read one
//					return _bufUnconsumedChars.charAt(_nextUnconsumedCharBufPos++);
//				}
//			}
			// 文字バッファからの読み込み
			for (;;) {
				if (_nextCharBufPos >= _charBufCharLength) {
					fill();	// 文字バッファへ読み込み
					if (_nextCharBufPos >= _charBufCharLength) {
						return -1;	// ファイル終端到達
					}
				}
				//--- skip LF
				if (_nextSkipLF) {
					_nextSkipLF = false;
					if (_bufCharArray[_nextCharBufPos] == '\n') {
						++_nextCharBufPos;
						continue;
					}
				}
				//--- read one
				return _bufCharArray[_nextCharBufPos++];
			}
		}
	}

	/**
	 * 配列の一部に一度だけ読み込みを行う。
	 * このメソッドは同期化されない。
	 * @param cbuf	転送先バッファ
	 * @param off	文字の格納開始オフセット
	 * @param len	読み込む文字の最大数
	 * @return	読み込まれた文字数、ストリームの終わりに達した場合は (-1)
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected int read1pass(char[] cbuf, int off, int len) throws IOException
	{
//		// 未消費バッファからの読み込み
//		if (_nextUnconsumedCharBufPos >= 0) {
//			if (_nextUnconsumedCharBufPos < _bufUnconsumedChars.length()) {
//				// 未消費バッファから最大読み込む
//				int read = 0;
//				if (_nextSkipLF) {
//					_nextSkipLF = false;
//					if (_bufUnconsumedChars.charAt(_nextUnconsumedCharBufPos) == '\n') {
//						++_nextUnconsumedCharBufPos;
//					}
//				}
//				if (_nextUnconsumedCharBufPos < _bufUnconsumedChars.length()) {
//					read = Math.min(len, _bufUnconsumedChars.length() - _nextUnconsumedCharBufPos);
//					_bufUnconsumedChars.getChars(_nextUnconsumedCharBufPos, _nextUnconsumedCharBufPos+read, cbuf, off);
//					_nextUnconsumedCharBufPos += read;
//				}
//				if (read > 0) {
//					// 読込が行われたら、このパスは終了
//					return read;
//				}
//			}
//			// 未消費文字バッファはすべて消費
//			_nextUnconsumedCharBufPos = (-1);
//			_nextCharBufPos = 0;	// 文字バッファの先頭から
//		}

		// 文字バッファからの読み込み
		if (_nextCharBufPos >= _charBufCharLength) {
			fill();	// 文字バッファへ読み込み
			if (_nextCharBufPos >= _charBufCharLength) {
				return -1;	// ファイル終端到達
			}
		}
		if (_nextSkipLF) {
			_nextSkipLF = false;
			if (_bufCharArray[_nextCharBufPos] == '\n') {
				++_nextCharBufPos;
				if (_nextCharBufPos >= _charBufCharLength) {
					fill();	// 文字バッファへ読み込み
					if (_nextCharBufPos >= _charBufCharLength) {
						return -1;	// ファイル終端到達
					}
				}
			}
		}
		int read = Math.min(len, _charBufCharLength - _nextCharBufPos);
		System.arraycopy(_bufCharArray, _nextCharBufPos, cbuf, off, read);
		_nextCharBufPos += read;
		return read;
	}

	/**
	 * 配列の一部に文字を読み込む。
	 * このメソッドは、<code>Reader</code> クラスの対応する <code>read</code> メソッドの汎用規約を実装する。
	 * より高い利便性のため、このメソッドはベースとなるストリームの <code>read</code> メソッドを繰り返し呼び出して、
	 * できるだけ多くの文字を読み込もうとする。この <code>read</code> の反復は、次の条件の 1 つが <tt>true</tt> になるまで行われる。
	 * <ul>
	 * <li>指定された文字数が読み込まれた
	 * <li>ベースとなるストリームの <code>read</code> メソッドが、ファイルの終わりを示す (-1) を返した
	 * <li>ベースとなるストリームの <code>ready</code> メソッドが、それ以上の入力要求がブロックされることを示す <tt>false</tt> を返した
	 * </ul>
	 * ベースとなるストリームの最初の <code>read</code> がファイルの終わりを示す (-1) を返すと、このメソッドは (-1) を返す。
	 * そうでない場合、このメソッドは実際に読み込まれた文字数を返す。
	 * @param cbuf	転送先バッファ
	 * @param off	文字の格納開始オフセット
	 * @param len	読み込む文字の最大数
	 * @return	読み込まれた文字数、ストリームの終わりに達した場合は (-1)
	 * @throws IOException	入出力エラーが発生した場合、またはストリームがすでに閉じている場合
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		synchronized (lock) {
			ensureOpened();
			//--- validation
			if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0))
			{
				throw new IndexOutOfBoundsException();
			}
			else if (len == 0) {
				return 0;
			}
			
			//--- read
			int read = read1pass(cbuf, off, len);
			if (read < 0) {
				return read;	// ストリーム終端に到達
			}
			while ((read < len) && ((_nextCharBufPos < _charBufCharLength) || (_stream.available() > 0))) {
				int n1 = read1pass(cbuf, off+read, len-read);
				if (n1 <= 0)
					break;
				read += n1;
			}
			return read;
		}
	}

	/**
	 * 指定された文字数分、文字をスキップする。
	 * @param n	スキップする文字数
	 * @return	実際にスキップした文字数
	 * @throws IllegalArgumentException	<em>n</em> が負の値の場合
	 * @throws IOException	入出力エラーが発生した場合、またはストリームがすでに閉じている場合
	 */
	@Override
	public long skip(long n) throws IOException
	{
		if (n < 0L) {
			throw new IllegalArgumentException("skip value is negative");
		}
		synchronized (lock) {
			ensureOpened();
			long r = n;
//			// 未消費文字バッファをスキップ
//			if (_nextUnconsumedCharBufPos >= 0) {
//				if (_nextUnconsumedCharBufPos < _bufUnconsumedChars.length()) {
//					if (_nextSkipLF) {
//						_nextSkipLF = false;
//						if (_bufUnconsumedChars.charAt(_nextUnconsumedCharBufPos) == '\n') {
//							++_nextUnconsumedCharBufPos;
//						}
//					}
//					if (_nextUnconsumedCharBufPos < _bufUnconsumedChars.length()) {
//						long d = _bufUnconsumedChars.length() - _nextUnconsumedCharBufPos;
//						if (r <= d) {
//							// 未消費文字バッファ内でスキップ
//							_nextUnconsumedCharBufPos += r;
//							return r;
//						}
//						else {
//							// 未消費文字バッファの文字数でも不足
//							r -= d;
//						}
//					}
//				}
//				// 未消費文字バッファはすべて消費
//				_nextUnconsumedCharBufPos = (-1);
//				_nextCharBufPos = 0;	// 文字バッファの先頭から
//			}
			// 文字バッファをスキップ
			while (r > 0) {
				if (_nextCharBufPos >= _charBufCharLength) {
					fill();
					if (_nextCharBufPos >= _charBufCharLength) {
						break;	// EOF
					}
				}
				if (_nextSkipLF) {
					_nextSkipLF = false;
					if (_bufCharArray[_nextCharBufPos] == '\n') {
						++_nextCharBufPos;
					}
				}
				long d = _charBufCharLength - _nextCharBufPos;
				if (r <= d) {
					_nextCharBufPos += r;
					r = 0;
					break;
				}
				else {
					r -= d;
					_nextCharBufPos = _charBufCharLength;
				}
			}
			return (n - r);
		}
	}

	/**
	 * このストリームが読み込み可能かどうかを判定する。
	 * バッファリングされた文字型ストリームは、空白ではないか、ベースとなる文字型ストリームが読み込み可能であるときに読み込み可能となる。
	 * @return	次の <code>read()</code> が入力をブロックしないことが確実な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<tt>false</tt> が返されても、次の読み込みが確実にブロックするというわけではない。
	 * @throws IOException	入出力エラーが発生した場合、またはストリームがすでに閉じている場合
	 */
	@Override
	public boolean ready() throws IOException
	{
		synchronized (lock) {
			ensureOpened();
			
//			if (_nextUnconsumedCharBufPos >= 0) {
//				if (_nextUnconsumedCharBufPos < _bufUnconsumedChars.length()) {
//					if (_nextSkipLF) {
//						_nextSkipLF = false;
//						if (_bufUnconsumedChars.charAt(_nextUnconsumedCharBufPos) == '\n') {
//							++_nextUnconsumedCharBufPos;
//						}
//					}
//					if (_nextUnconsumedCharBufPos < _bufUnconsumedChars.length()) {
//						// 文字はブロックせずに読み込み可能
//						return true;
//					}
//				}
//				// 未消費文字バッファはすべて消費
//				_nextUnconsumedCharBufPos = (-1);
//				_nextCharBufPos = 0;	// 文字バッファの先頭から
//			}
			
			if (_nextSkipLF) {
				if (_nextCharBufPos >= _charBufCharLength && (_stream.available() > 0)) {
					fill();
				}
				if (_nextCharBufPos < _charBufCharLength) {
					_nextSkipLF = false;
					if (_bufCharArray[_nextCharBufPos] == '\n') {
						++_nextCharBufPos;
					}
				}
			}
			return ((_nextCharBufPos < _charBufCharLength) || (_stream.available() > 0));
		}
	}

	/**
	 * このストリームが、実行する <code>mark()</code> オペレーションをサポートするかどうかを取得する。
	 * @return	このストリームが <code>mark()</code> オペレーションをサポートしている場合は <tt>true</tt>
	 */
	@Override
	public boolean markSupported() {
		return true;
	}

	/**
	 * ストリームの現在位置にマークを設定する。
	 * 以降の <code>reset()</code> の呼び出しでは、この位置へのストリームの再配置が試みられる。
	 * @param readAheadLimit	マークを保持しながら読み込むことができる文字数の上限。
	 * 							この上限値の前後の数の文字を読み込んだあとでストリームをリセットしようとすると失敗する場合がある。
	 * 							入力バッファのサイズより大きい限界値を指定すると、そのサイズが限界より小さくない新しいバッファが割り当てられる。
	 * 							そのため、大きな値は注意して使用する必要がある。
	 * @throws IllegalArgumentException	<em>readAheadLimit</em> &lt; 0 の場合
	 * @throws IOException	入出力エラーが発生した場合、またはストリームがすでに閉じている場合
	 */
	@Override
	public void mark(int readAheadLimit) throws IOException
	{
		if (readAheadLimit < 0) {
			throw new IllegalArgumentException("Read-ahead limit < 0");
		}
		synchronized (lock) {
			ensureOpened();
			_readAheadLimit = readAheadLimit;
//			if (_nextUnconsumedCharBufPos >= 0 && _nextUnconsumedCharBufPos < _bufUnconsumedChars.length()) {
//				// 未消費バッファ内の位置
//				_markedUnconsumedCharBufPos = _nextUnconsumedCharBufPos;
//				_markedCharBufPos = UNMARKED;
//			}
//			else {
				// 文字バッファの位置
//				_markedUnconsumedCharBufPos = UNMARKED;
				_markedCharBufPos = _nextCharBufPos;
//			}
			_markedSkipLF = _nextSkipLF;
		}
	}

	/**
	 * ストリームを、もっとも新しいマーク位置にリセットする。
	 * @throws IOException	ストリームにマークを設定できなかった場合、またはマークが無効になった場合、またはストリームがすでに閉じている場合
	 */
	@Override
	public void reset() throws IOException
	{
		synchronized (lock) {
			ensureOpened();
			if (/*_markedUnconsumedCharBufPos < 0 && */_markedCharBufPos < 0) {
				throw new IOException(_markedCharBufPos==INVALIDATED ? "Mark invalid" : "Stream not marked");
			}
			
//			if (_markedUnconsumedCharBufPos >= 0) {
//				// 未消費バッファ内の位置
//				_nextUnconsumedCharBufPos = _markedUnconsumedCharBufPos;
//				_nextCharBufPos = 0;
//			}
//			else {
				// 文字バッファの位置
//				_nextUnconsumedCharBufPos = (-1);
				_nextCharBufPos = _markedCharBufPos;
//			}
			_nextSkipLF = _markedSkipLF;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void ensureOpened() throws IOException
	{
		if (_channel == null)
			throw new IOException("Stream closed");
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
	 * 指定されたファイルを開き、内部ステータスを初期化する。
	 * @throws FileNotFoundException	指定されたファイルが存在しない場合
	 * @throws IllegalStateException	ファイルがすでにオープンされている場合
	 */
	protected void open() throws FileNotFoundException
	{
		// check
		Validations.validState(!isOpen(), "Already opened the File : \"" + _file.getAbsolutePath() + "\"");
		
		// open
		_stream = new FileInputStream(_file);
		_channel = _stream.getChannel();
		_eof = false;
		
		// initialize buffers
		_bufReadable = false;	// バッファは書き込み可能
		_bufRead.clear();
		_bufChar.clear();
		
		// initialize states
		_actualReadBytes            = 0L;
		_readBufHeadFilePos			= 0L;		// 初期位置
		_charBufHeadFilePos			= 0L;		// 初期位置
		_charBufByteLength			= 0;		// バッファは空
		_charBufCharLength			= 0;		// バッファは空
//		_prevCharBufHeadFilePos		= 0;		// 不定
//		_prevCharBufByteLength		= 0;		// 不定
//		_prevCharBufConsumedLength	= 0;		// 不定
//		_nextUnconsumedCharBufPos	= -1;		// 無効
		_nextCharBufPos				= 0;		// 初期位置
		_nextSkipLF					= false;	// 対象外
//		_markedUnconsumedCharBufPos	= UNMARKED;	// マーク無効
		_markedCharBufPos			= UNMARKED;	// マーク無効
		_markedSkipLF				= false;	// マーク無効
		_readAheadLimit				= 0;		// マーク無効
	}
	
	/**
	 * バッファの最大容量まで、ストリームを読み込む。
	 * このメソッドを呼び出す時点で、文字バッファの内容がすべて消費されたことを前提としている。
	 * マークがない場合は、バッファの内容がすべて破棄される。
	 * このメソッドは同期化されない。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void fill() throws IOException
	{
		/* debug */
		{
			System.err.println("[Debug] BufferedTextFileReader.fill() called");
			System.err.println("  read bytes = " + getReadBytes());
			System.err.flush();
		}
		/* end of debug */
		
		int dstCharPos;
		if (_markedCharBufPos <= UNMARKED/* && _markedUnconsumedCharBufPos <= UNMARKED*/) {
			// No mark
			dstCharPos = 0;
		}
//		else if (_markedUnconsumedCharBufPos >= 0) {
//			// 未消費文字バッファをマーク
//			long delta = (_bufUnconsumedChars.length() - _markedUnconsumedCharBufPos) + _nextCharBufPos;
//			if (delta >= _readAheadLimit) {
//				// 次の読み込み位置が readAheadLimit を超えているので、Invalid mark
//				_markedUnconsumedCharBufPos = INVALIDATED;
//				_markedCharBufPos = INVALIDATED;
//				_readAheadLimit = 0;
//				/* debug */
//				{
//					System.err.println("[Debug] BufferedTextFileReader.fill() - unconsumed : mark invalidated");
//				}
//				/* end of debug */
//			}
//			else {
//				// 未消費文字バッファを更新
//				_bufUnconsumedChars.delete(0, _markedUnconsumedCharBufPos);
//				_prevCharBufConsumedLength += _markedUnconsumedCharBufPos;
//				_markedUnconsumedCharBufPos = 0;
//				//--- 現在の文字バッファの内容をすべて未消費文字バッファに追加
//				_bufUnconsumedChars.append(_bufCharArray, 0, _charBufCharLength);
//				//--- 未消費文字バッファのファイル位置等は、以前のもので OK(未消費文字バッファに追加しているので)
//				
//				// 新しいバッファ容量を考慮
//				long curcap = (long)_bufUnconsumedChars.length() + (long)_bufChar.capacity();
//				if (curcap < _readAheadLimit) {
//					// 容量拡張
//					int newcap = (int)(_readAheadLimit - curcap) + _bufChar.capacity();
//					_bufCharArray = new char[newcap];
//					_bufChar = CharBuffer.wrap(_bufCharArray);
//					/* debug */
//					{
//						System.err.println("[Debug] BufferedTextFileReader.fill() unconsumed 容量拡張");
//						System.err.println("  new capacity = " + newcap);
//						System.err.flush();
//					}
//					/* end of debug */
//				}
//			}
//		}
		else {
			// 文字バッファをマーク
			int delta = _nextCharBufPos - _markedCharBufPos;
			if (delta >= _readAheadLimit) {
				// 次の読み込み位置が readAheadLimit を超えているので、Invalid mark
//				_markedUnconsumedCharBufPos = INVALIDATED;
				_markedCharBufPos = INVALIDATED;
				_readAheadLimit = 0;
				dstCharPos = 0;
				/* debug */
				{
					System.err.println("[Debug] BufferedTextFileReader.fill() - mark invalidated");
				}
				/* end of debug */
			}
//			else if (_markedCharBufPos >= _charBufCharLength) {
//				// マーク位置が文字バッファの終端に到達している場合は、更新された文字バッファの先頭位置とする
//				_markedUnconsumedCharBufPos = UNMARKED;
//				_markedCharBufPos = 0;
//				_nextUnconsumedCharBufPos = (-1);
//				
//				// 新しいバッファ容量を考慮
//				if (_bufChar.capacity() < _readAheadLimit) {
//					// 容量を拡張
//					_bufCharArray = new char[_readAheadLimit];
//					_bufChar = CharBuffer.wrap(_bufCharArray);
//					/* debug */
//					{
//						System.err.println("[Debug] BufferedTextFileReader.fill() マーク位置＝文字バッファ終端 ： 容量拡張");
//						System.err.println("  new capacity = " + _readAheadLimit);
//						System.err.flush();
//					}
//					/* end of debug */
//				}
//			}
			else {
//				// 現在の文字バッファの内容を、マーク位置以降から未消費文字バッファへ移行
//				if (_bufUnconsumedChars == null) {
//					_bufUnconsumedChars = new StringBuilder();
//				}
//				_bufUnconsumedChars.setLength(0);
//				_bufUnconsumedChars.append(_bufCharArray, _markedCharBufPos, _charBufCharLength-_markedCharBufPos);
//				_prevCharBufHeadFilePos    = _charBufHeadFilePos;
//				_prevCharBufByteLength     = _charBufByteLength;
//				_prevCharBufConsumedLength = _markedCharBufPos;
//				_nextUnconsumedCharBufPos  = _nextCharBufPos - _markedCharBufPos;
//				_markedUnconsumedCharBufPos = 0;
//				_markedCharBufPos = UNMARKED;
//				
//				// 新しいバッファ容量を考量
//				long curcap = (long)_bufUnconsumedChars.length() + (long)_bufChar.capacity();
//				if (curcap < _readAheadLimit) {
//					// 容量拡張
//					int newcap = (int)(_readAheadLimit - curcap) + _bufChar.capacity();
//					_bufCharArray = new char[newcap];
//					_bufChar = CharBuffer.wrap(_bufCharArray);
//					/* debug */
//					{
//						System.err.println("[Debug] BufferedTextFileReader.fill() 容量拡張");
//						System.err.println("  new capacity = " + newcap);
//						System.err.flush();
//					}
//					/* end of debug */
//				}
				
				// 拡張容量を計算
				long needlimit = (long)_markedCharBufPos + (long)_readAheadLimit;
				if (needlimit >= Integer.MAX_VALUE) {
					// 容量オーバーのため、マーク無効
					_markedCharBufPos = INVALIDATED;
					_readAheadLimit = 0;
					dstCharPos = 0;
					/* debug */
					{
						System.err.println("[Debug] BufferedTextFileReader.fill() - mark invalidated - could not expand memory");
					}
					/* end of debug */
				}
				else {
					// 容量拡張
					int newcap = _markedCharBufPos + _readAheadLimit;
					char[] newarray = new char[newcap];
					System.arraycopy(_bufCharArray, 0, newarray, 0, _charBufCharLength);
					_bufCharArray = newarray;
					_bufChar = CharBuffer.wrap(newarray);
					dstCharPos = _charBufCharLength;
				}
			}
		}
		
		// 文字バッファの初期化
		if (dstCharPos > 0) {
			// 読み込み済み文字データは維持されている
			_nextCharBufPos = dstCharPos;
			_bufChar.limit(_bufChar.capacity());
			_bufChar.position(dstCharPos);
		}
		else {
			// 文字バッファをクリア
			//_readBufHeadFilePos += _charBufByteLength;
			_charBufHeadFilePos += _charBufByteLength;
			_charBufByteLength  = 0;
			_charBufCharLength  = 0;
			_nextCharBufPos     = 0;
			_bufChar.clear();
		}
		
		// ファイルからバイトバッファに読み込む
		if (!_eof) {
			if (_bufReadable) {
				// デコードの残りバイトを保持
				if (_bufRead.hasRemaining()) {
					_readBufHeadFilePos += _bufRead.position();
					_bufRead.compact();
					//_bufRead.flip();
				}
				else {
					_readBufHeadFilePos = _channel.position();
					_bufRead.clear();
				}
				_bufReadable = false;
			}
			//--- 読み込み
			int read;
			do {
				read = _channel.read(_bufRead);
			} while (read == 0);
			
			// デコード
			_bufRead.flip();
			_bufReadable = true;
			if (read < 0) {
				// ファイル終端に到達
				_eof = true;
				_decoder.decode(_bufRead, _bufChar, true);
				_decoder.flush(_bufChar);
				_decoder.reset();
			}
			else {
				_actualReadBytes += read;
				_decoder.decode(_bufRead, _bufChar, false);
			}
			
			// デコード結果の更新
			_charBufByteLength += _bufRead.position();
			_charBufCharLength = _bufChar.position();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
