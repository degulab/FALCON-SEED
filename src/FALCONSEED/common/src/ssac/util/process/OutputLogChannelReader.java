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
 * @(#)OutputLogChannelReader.java	3.0.0	2014/03/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

/**
 * 標準出力、標準エラー出力の内容を、指定されたログチャネルから読み込むクラス。
 * <p>ログファイルの構造はCSVファイルであり、第１列は種別、第２列はデータとなる。
 * 種別には、標準出力(=1)、標準エラー出力(=2)のどちらかを記述する。
 * データは改行なども含まれるため、基本的にダブルクオートでエスケープする。
 * 
 * @version 3.0.0	2014/03/12
 * @since 3.0.0
 */
public class OutputLogChannelReader implements OutputLogChannelAccessor
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static protected final int	DEFAULT_CHARBUFFER_SIZE	= 2;
	static protected final int	DEFAULT_BYTEBUFFER_SIZE	= 8192;

	static protected final int	TOKEN_EOF			= (-1);
	static protected final int	TOKEN_PENDING		= 0;
	static protected final int	TOKEN_DELIM_FIELD	= 1;
	static protected final int	TOKEN_DELIM_RECORD	= 2;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 操作対象のファイルチャネル **/
	private OutputLogChannel	_logChannel;
	/** 読み込み用デコーダー **/
	private CharsetDecoder		_decoder;
	/** 文字バッファ **/
	private CharBuffer			_charBuffer;
	/** バイトバッファ **/
	private ByteBuffer			_byteBuffer;
	/** 次に読み込むファイル位置 **/
	private long				_nextReadPos;
	/** 取得済みのレコード **/
	private OutputString		_nextString;

	/** フィールドの内容を保持するバッファ **/
	private StringBuilder	_buffer;
	/** ログ種別のフィールド値 **/
	private String			_fldType;
	/** ログデータのフィールド値 **/
	private String			_fldData;

	private boolean		_enquoted	= false;
	private boolean		_dequoting	= false;
	private boolean		_skipLF		= false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	protected OutputLogChannelReader(OutputLogChannel channel) {
		_logChannel = channel;
		_decoder = _logChannel.newDecoder();
		_charBuffer = CharBuffer.wrap(new char[DEFAULT_CHARBUFFER_SIZE]);
		_byteBuffer = ByteBuffer.wrap(new byte[DEFAULT_BYTEBUFFER_SIZE]);
		_charBuffer.flip();
		_byteBuffer.flip();
		_nextReadPos = 0L;
		_nextString = null;
		_buffer   = new StringBuilder();
		_fldType  = null;
		_fldData  = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

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
	 * このリーダーオブジェクトがオープンされているか判定する。
	 * @return	オープンされていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isOpened() {
		synchronized (_logChannel._lock) {
			return (_decoder != null);
		}
	}

	/**
	 * リーダーをクローズする。
	 * ストリームへのアクセサが存在しない場合は、ストリームを閉じる。
	 */
	public void close() {
		synchronized (_logChannel._lock) {
			if (_decoder == null)
				return;
			_decoder.reset();
			_decoder = null;
			_charBuffer.clear();
			_charBuffer = null;
			_byteBuffer.clear();
			_byteBuffer = null;
			_nextString = null;
			_fldType = null;
			_fldData = null;
			_logChannel.closeAccessor(this);
		}
	}

	/**
	 * 読み込み可能な文字が存在するかどうかを判定する。
	 * このメソッドでは、次のレコードが取得可能な状態かどうかはわからない。
	 * @return	読み込み可能な文字があれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IOException	リーダーが閉じられている場合
	 */
	public boolean ready() throws IOException
	{
		synchronized (_logChannel._lock) {
			ensureOpen();
			return charReady();
		}
	}
	
	public boolean hasNext() throws IOException
	{
		synchronized (_logChannel._lock) {
			ensureOpen();
			if (_nextString == null) {
				_nextString = readNextRecord();
			}
			return (_nextString != null);
		}
	}

	public OutputString readNext() throws IOException
	{
		synchronized (_logChannel._lock) {
			ensureOpen();
			OutputString retString;
			if (_nextString != null) {
				retString = _nextString;
				_nextString = null;
			} else {
				retString = readNextRecord();
			}
			return retString;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected OutputString readNextRecord() throws IOException
	{
		for (;;) {
			int ch = readNextToken();

			if (ch==OutputLogUtil.FIELD_DELIMITER_CHAR || ch=='\n' || ch < 0) {
				// delimiter received
				//--- save field data
				if (_fldType == null) {
					_fldType = _buffer.toString();
				}
				else if (_fldData == null) {
					_fldData = _buffer.toString();
				}
				// else : drop other fields
				_buffer.setLength(0);	// clear field buffer

				if (ch=='\n' || ch < 0) {
					// record delimiter received
					//--- get data and clear
					Boolean isError;
					if (_fldType != null && _fldType.length() > 0) {
						isError = OutputLogUtil.LOGTYPE_STDERR.equals(_fldType);
					} else {
						isError = null;
					}
					String str = (_fldData==null ? "" : _fldData);
					_fldType = null;
					_fldData = null;
					//--- get record item
					if (isError != null) {
						OutputString ostr = new OutputString(isError, str);
						return ostr;
					}
					// else : skip blank record
				}
			}
			else {
				// pending receive
				break;
			}
		}

		// no record item
		return null;
	}

	protected int readNextToken() throws IOException
	{
		int ch = 0;

		for(; charReady(); ) {
			ch = readChar();

			if (_skipLF) {
				// skip LineFeed
				_skipLF = false;
				if (ch == '\n') {
					ch = 1;	// pending
					continue;
				}
			}

			if (_enquoted) {
				if (ch < 0) {
					_enquoted = false;
					_dequoting = false;
					break;
				}
				else if (ch == OutputLogUtil.FIELD_ENQUOTE_CHAR) {
					if (_dequoting) {
						// escaped enquote character
						_dequoting = false;
						_buffer.append(OutputLogUtil.FIELD_ENQUOTE_CHAR);
						continue;
					} else {
						// marked dequote
						_dequoting = true;
						continue;
					}
				}
				else if (!_dequoting) {
					// text character in quote
					_buffer.append((char)ch);
					ch = 1;	// pending
					continue;
				}
				else {
					// enquote finished
					_enquoted = false;
				}
			}

			// csv element
			if (ch == OutputLogUtil.FIELD_DELIMITER_CHAR) {
				// field delimiter
				return ch;
			}
			else if (ch == OutputLogUtil.FIELD_ENQUOTE_CHAR) {
				// start enquote
				_enquoted = true;
			}
			else if (ch == '\r') {
				// Record delimiter (CR)
				_skipLF = true;
				return '\n';
			}
			else if (ch == '\n') {
				// Record delimiter (LF)
				return ch;
			}
			else {
				// text character in field
				_buffer.append((char)ch);
				ch = 1;	// pending
			}
		}

		return ch;
	}

	protected int readChar() throws IOException
	{
		// read from char-buffer
		if (_charBuffer.hasRemaining()) {
			return _charBuffer.get();
		}

		// read from bytes
		boolean eoi = false;
		_charBuffer.clear();
		try {
			for (;;) {
				CoderResult localCoderResult = _decoder.decode(_byteBuffer, _charBuffer, eoi);
				if (localCoderResult.isUnderflow()) {
					if (eoi) {
						// ファイル終端に到達したため、フラッシュ
						_decoder.flush(_charBuffer);
						break;
					}

					if ((_charBuffer.position() > 0) && (availableReadBytes() > 0)) {
						// 読み込まれた文字があり、まだ読み込むものが残っている場合は、変換終了
						break;
					}

					// 新しいバイトを読み込む
					int read = readBytes();
					if (read < 0) {
						// ファイル終端に到達
						eoi = true;
					}

					// デコーダーをリセット
					_decoder.reset();
				}
				else if (localCoderResult.isOverflow()) {
					if (_charBuffer.position() > 0) {
						// オーバーフローでも、読み込み可能な文字があれば、変換終了
						break;
					}
					throw new AssertionError("Failed to decord bytes, CharBuffer is overflow!");
				}
			}
		}
		finally {
			_charBuffer.flip();
		}

		// ファイル終端に到達していれば、デコーダーをリセット
		if (eoi) {
			_decoder.reset();
		}

		// 変換文字の取得
		if (_charBuffer.hasRemaining()) {
			return _charBuffer.get();
		} else {
			// ファイル終端に到達
			return (-1);
		}
	}

	protected boolean charReady() throws IOException
	{
		if (_charBuffer.hasRemaining()) {
			return true;	// has characters
		} else {
			return (availableReadBytes() > 0);
		}
	}

	protected int readBytes() throws IOException
	{
		_byteBuffer.compact();
		int read;
		try {
			read = _logChannel._channel.read(_byteBuffer, _nextReadPos);
			if (read < 0) {
				return read;
			}
			_nextReadPos += read;
		}
		finally {
			_byteBuffer.flip();
		}

		read = _byteBuffer.remaining();
		return read;
	}

	protected int availableReadBytes() throws IOException
	{
		int readable = 0;
		if (_byteBuffer.hasRemaining()) {
			readable = _byteBuffer.remaining();
		} else {
			long rem = _logChannel._channel.size() - _nextReadPos;
			int bufcap = _byteBuffer.capacity() - _byteBuffer.limit();
			if (rem < bufcap) {
				readable = (int)rem;
			} else {
				readable = bufcap;
			}
		}
		return readable;
	}

	private void ensureOpen() throws IOException
	{
		if (_decoder == null) {
			throw new IOException("Stream for Read closed");
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
