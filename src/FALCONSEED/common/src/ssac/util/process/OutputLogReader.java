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
 * @(#)OutputLogReader.java	3.0.0	2014/03/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 標準出力、標準エラー出力の内容を記録したログファイルを読み込むクラス。
 * <p>ログファイルの構造はCSVファイルであり、第１列は種別、第２列はデータとなる。
 * 種別には、標準出力(=1)、標準エラー出力(=2)のどちらかを記述する。
 * データは改行なども含まれるため、基本的にダブルクオートでエスケープする。
 * 
 * @version 3.0.0	2014/03/11
 * @since 3.0.0
 */
public class OutputLogReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int	TOKEN_EOF			= (-1);
	static protected final int	TOKEN_PENDING		= 0;
	static protected final int	TOKEN_DELIM_FIELD	= 1;
	static protected final int	TOKEN_DELIM_RECORD	= 2;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ログファイル **/
	private final File	_logFile;
	
	/** 同期用オブジェクト **/
	private Object			_lock;
	/** 読み込みストリーム **/
	private BufferedReader	_reader;
	/** 読み込み時のエンコーディングとなる文字セット名 **/
	private String			_encoding;

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
	
	public OutputLogReader(File logFile) throws FileNotFoundException
	{
		if (logFile==null)
			throw new NullPointerException("Log file object is null.");
		_logFile = logFile;
		InputStreamReader isr = new InputStreamReader(new FileInputStream(logFile));
		init(isr, isr.getEncoding());
	}
	
	public OutputLogReader(File logFile, Charset encoding) throws FileNotFoundException
	{
		if (logFile==null)
			throw new NullPointerException("Log file object is null.");
		_logFile = logFile;
		InputStreamReader isr = new InputStreamReader(new FileInputStream(logFile), encoding);
		init(isr, isr.getEncoding());
	}
	
	public OutputLogReader(File logFile, String encoding) throws FileNotFoundException, UnsupportedEncodingException
	{
		if (logFile==null)
			throw new NullPointerException("Log file object is null.");
		_logFile = logFile;
		InputStreamReader isr = new InputStreamReader(new FileInputStream(logFile), encoding);
		init(isr, (encoding==null ? isr.getEncoding() : encoding));
	}
	
	private void init(Reader reader, String actualEncoding) {
		_encoding = actualEncoding;
		_reader   = new BufferedReader(reader);
		_lock     = _reader;
		_buffer   = new StringBuilder();
		_fldType  = null;
		_fldData  = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getFile() {
		return _logFile;
	}
	
	public String getEncoding() {
		return _encoding;
	}
	
	public void close() {
		try {
			synchronized (_lock) {
				if (_reader != null) {
					_reader.close();
					_reader = null;
				}
			}
		}
		catch (IOException ignoreEx) {}
	}
	
	public OutputString readNext() throws IOException
	{
		synchronized (_lock) {
			ensureOpen();
			return readNextRecord();
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
		
		for(; _reader.ready(); ) {
			ch = _reader.read();
			
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
    
    private void ensureOpen() throws IOException
    {
    	if (_reader == null) {
    		throw new IOException("Stream closed");
    	}
    }

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
