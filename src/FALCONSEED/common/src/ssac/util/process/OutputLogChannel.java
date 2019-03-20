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
 * @(#)OutputLogChannel.java	3.0.0	2014/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * 標準出力、標準エラー出力の内容のログファイルにアクセスするファイルチャネルを保持するクラス。
 * 書き込み、および読み込みが同時に実行可能なインタフェースを提供する。
 * <p>ログファイルの構造はCSVファイルであり、第１列は種別、第２列はデータとなる。
 * 種別には、標準出力(=1)、標準エラー出力(=2)のどちらかを記述する。
 * データは改行なども含まれるため、基本的にダブルクオートでエスケープする。
 * 
 * @version 3.0.0	2014/03/24
 * @since 3.0.0
 */
public class OutputLogChannel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 出力先ファイルの抽象パス **/
	private final File	_logFile;

	/** 同期用オブジェクト **/
	protected Object			_lock;
	/** ファイルチャネル **/
	protected FileChannel		_channel;
	/** ランダムアクセスファイルのストリーム **/
	private RandomAccessFile	_stream;
	/** 入出力のエンコーディングに使用する文字セット **/
	private Charset				_charset;
	/** このオブジェクトのエンコーディングに使用される文字セット名 **/
	private String				_csName;

	/** このオブジェクトのクローズ処理が実行中であることを示すフラグ **/
	private boolean				_closing = false;
	/** 発行済みリーダー **/
	private OutputLogChannelReader	_reader;
	/** 発行済みライター **/
	private OutputLogChannelWriter	_writer;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public OutputLogChannel(File logFile) throws FileNotFoundException
	{
		this(logFile, false);
	}
	
	public OutputLogChannel(File logFile, Charset encoding) throws FileNotFoundException
	{
		this(logFile, false, encoding);
	}
	
	public OutputLogChannel(File logFile, String encoding) throws FileNotFoundException, UnsupportedEncodingException
	{
		this(logFile, false, encoding);
	}
	
	public OutputLogChannel(File logFile, boolean append) throws FileNotFoundException
	{
		this(append, logFile);
		_charset = Charset.defaultCharset();
		_csName  = _charset.name();
	}
	
	public OutputLogChannel(File logFile, boolean append, Charset encoding) throws FileNotFoundException
	{
		this(append, logFile);
		_charset = (encoding==null ? Charset.defaultCharset() : encoding);
		_csName  = _charset.name();
	}
	
	public OutputLogChannel(File logFile, boolean append, String encoding) throws FileNotFoundException, UnsupportedEncodingException
	{
		this(append, logFile);
		if (encoding == null) {
			_charset = Charset.defaultCharset();
			_csName  = _charset.name();
		} else {
			try {
				if (Charset.isSupported(encoding)) {
					_charset = Charset.forName(encoding);
					_csName  = encoding;
				} else {
					throw new UnsupportedEncodingException(encoding);
				}
			}
			catch (IllegalCharsetNameException ex) {
				throw new UnsupportedEncodingException(encoding);
			}
			catch (UnsupportedCharsetException ex) {
				throw new UnsupportedEncodingException(encoding);
			}
		}
	}
	
	protected OutputLogChannel(boolean append, File logFile) throws FileNotFoundException
	{
		if (logFile == null)
			throw new NullPointerException("Log file object is null.");
		_logFile = logFile;
		
		// create stream and channel
		_stream = new RandomAccessFile(logFile, "rw");
		_channel = _stream.getChannel();
		
		// set Lock object
		_lock = _channel;
		
		// truncate
		if (!append) {
			try {
				_channel.truncate(0L);
			} catch (IOException ex) {
				throw new FileNotFoundException("Failed to truncate after open file : " + ex.toString());
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getFile() {
		return _logFile;
	}
	
	public String getEncoding() {
		return _csName;
	}
	
	public Charset getCharset() {
		return _charset;
	}

	/**
	 * このチャネルオブジェクトがオープンされているか判定する。
	 * @return	オープンされていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isOpened() {
		synchronized (_lock) {
			return (_channel != null);
		}
	}
	
	public void close()
	{
		synchronized (_lock) {
			_closing = true;
			
			// アクセサがあれば、閉じる
			if (_reader != null) {
				_reader.close();
				_reader = null;
			}
			if (_writer != null) {
				_writer.close();
				_writer = null;
			}

			// このオブジェクトのストリームを閉じる
			if (_channel != null) {
				try {
					_channel.close();
				} catch (IOException ex) {}
				_channel = null;
			}
			if (_stream != null) {
				try {
					_stream.close();
				} catch (IOException ex) {}
			}
			
			_closing = false;
		}
	}
	
	public OutputLogChannelReader getReader() throws IOException
	{
		synchronized (_lock) {
			if (_channel != null) {
				// get or create reader
				if (_reader == null) {
					_reader = new OutputLogChannelReaderImpl(this);
				}
				return _reader;
			}
			
			// no channel
			throw new ClosedChannelException();
		}
	}
	
	public OutputLogChannelWriter getWriter() throws IOException
	{
		synchronized (_lock) {
			if (_channel != null) {
				// get or create writer
				if (_writer == null) {
					_writer = new OutputLogChannelWriterImpl(_lock, this);
				}
				return _writer;
			}
			if (_channel == null) {
				// no channel
				return null;
			}
			
			// no channel
			throw new ClosedChannelException();
		}
	}

	//------------------------------------------------------------
	// Package interfaces
	//------------------------------------------------------------
	
	FileChannel getChannel() {
		return _channel;
	}
	
	Object getLock() {
		return _lock;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected CharsetEncoder newEncoder() {
		return _charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
	}
	
	protected CharsetDecoder newDecoder() {
		return _charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
	}
	
	protected void closeAccessor(OutputLogChannelAccessor accessor) {
		if (accessor != null && !_closing) {
			boolean closedAccessor = false;
			if (_reader == accessor) {
				_reader = null;
				closedAccessor = true;
			}
			else if (_writer == accessor) {
				_writer = null;
				closedAccessor = true;
			}
			
			// 管理するアクセサがすべて閉じられた場合は、このオブジェクトのストリームをクローズする
			if (closedAccessor && _reader==null && _writer==null) {
				close();
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class OutputLogChannelReaderImpl extends OutputLogChannelReader
	{
		public OutputLogChannelReaderImpl(OutputLogChannel channel) {
			super(channel);
		}
	}
	
	protected class OutputLogChannelWriterImpl extends OutputLogChannelWriter
	{
		public OutputLogChannelWriterImpl(Object lockObject, OutputLogChannel channel) {
			super(lockObject, channel);
		}
	}
}
