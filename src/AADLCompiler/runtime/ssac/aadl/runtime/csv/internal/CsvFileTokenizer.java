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
 * @(#)CsvFileTokenizer.java	1.90	2013/08/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.csv.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;
import java.util.List;

import ssac.aadl.runtime.io.internal.ITokenPosition;
import ssac.aadl.runtime.io.internal.TextFileLineTokenizer;

/**
 * CSV形式のテキストファイルを、ファイル先頭からトークン分割するクラス。
 * 
 * @version 1.90	2013/08/07
 * @since 1.90
 */
public class CsvFileTokenizer extends TextFileLineTokenizer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** CSVフィールドを解析するデコーダー **/
	private CsvFieldDecoder	_decoder;

	/** 読み込み済みレコード先頭のファイルインデックス **/
	private long		_recordBeginIndex;
	/** 読み込み済みレコード終端のファイルインデックス **/
	private long		_recordEndIndex;
	/** 現在までのレコード数 **/
	private long		_recordCount;
	/** 現在までのレコード最大バイト数 **/
	private int			_maxRecordBytes;
	/** 現在までのレコード最大文字数 **/
	private int			_maxRecordChars;
	/** 現在までのレコード最大フィールド数 **/
	private int			_maxRecordFields;
	/** 読み込み済みレコードのフィールド分割前の文字列 **/
	private String		_recordString;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public CsvFileTokenizer(File file) throws FileNotFoundException
	{
		super(file);
		setup();
	}

	public CsvFileTokenizer(File file, int bufferSize) throws FileNotFoundException
	{
		super(file, bufferSize);
		setup();
	}

	public CsvFileTokenizer(File file, String charsetName)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		super(file, charsetName);
		setup();
	}

	public CsvFileTokenizer(File file, String charsetName, int bufferSize)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		super(file, charsetName, bufferSize);
		setup();
	}

	public CsvFileTokenizer(File file, Charset encoding)
		throws FileNotFoundException
	{
		super(file, encoding);
		setup();
	}
	
	public CsvFileTokenizer(File file, Charset encoding, int bufferSize)
		throws FileNotFoundException
	{
		super(file, encoding, bufferSize);
		setup();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 現在の CSV フォーマットを返す。
	 * @return <code>CsvFormat</code> オブジェクト
	 */
	public CsvFormat getCsvFormat() {
		return getCsvFieldDecoder().getCsvFormat();
	}

	/**
	 * 新しい CSV フォーマットを設定する。
	 * @param newFormat	<code>CsvFormat</code> オブジェクト
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean setCsvFormat(final CsvFormat newFormat) {
		return getCsvFieldDecoder().setCsvFormat(newFormat);
	}

	/**
	 * 最後に読み込まれた CSV レコード先頭のファイル上の位置を返す。
	 * @return	ファイル上の CSV レコード開始位置となるインデックス
	 */
	public long getRecordBeginIndex() {
		return _recordBeginIndex;
	}

	/**
	 * 最後に読み込まれた CSV レコード終端のファイル上の位置を返す。
	 * @return	ファイル上の CSV レコード終端位置の次の位置となるインデックス
	 */
	public long getRecordEndIndex() {
		return _recordEndIndex;
	}

	/**
	 * 最後に読み込まれた CSV レコードのバイト数を返す。
	 * @return	最後に読み込まれた CSV レコードのバイト数
	 */
	public long getRecordByteSize() {
		return (_recordEndIndex - _recordBeginIndex);
	}

	/**
	 * CSV レコードの最大バイト数を返す。
	 * @return	読み込まれた CSV レコードの最大バイト数
	 */
	public int getMaxReocrdByteSize() {
		return _maxRecordBytes;
	}

	/**
	 * 読み込まれた CSV レコード数を返す。
	 * @return	読み込まれた CSV レコード数
	 */
	public long getRecordCount() {
		return _recordCount;
	}

	/**
	 * CSV レコードの最大文字数を返す。
	 * @return	読み込まれた CSV レコードの最大文字数
	 */
	public int getMaxRecordLength() {
		return _maxRecordChars;
	}

	/**
	 * CSV レコードの最大フィールド数を返す。
	 * @return	読み込まれた CSV レコードの最大フィールド数
	 */
	public int getMaxFieldCount() {
		return _maxRecordFields;
	}

	/**
	 * 最後に読み込まれた CSV レコードの文字列を返す。
	 * この文字列は読み込まれたままの文字列となる。
	 * @return	CSV レコードの文字列を返す。
	 */
	public String getRecordString() {
		return _recordString;
	}

	/**
	 * 次のレコード先頭読み込み位置を、指定された位置に設定する。
	 * この操作では、現在の読み込み状態を破棄し、新しい読み込み位置を設定する。
	 * @param position	新しい読み込み開始位置とする位置オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	引数がこのクラスの位置情報オブジェクトではない場合
	 * @throws IllegalArgumentException	指定された位置が適切な位置情報ではない場合
	 * @throws ClosedChannelException	現在のチャネルがクローズしている場合
	 * @throws IOException	その他の入出力エラーが発生した場合
     */
	@Override
	public void seekToReadPosition(ITokenPosition position) throws IOException
	{
		CsvTokenPosition pos = (CsvTokenPosition)position;

		super.seekToReadPosition(position);
		
		_recordCount = pos.getLastRecordCount();
		_maxRecordFields = pos.getLastMaxFieldCount();
	}

	/**
	 * 次の CSV レコードを読み込む。
	 * @return	読み込まれた CSV レコードに含まれるフィールドのリストを返す。
	 * 			ストリームの終端に到達している場合は <tt>null</tt> を返す。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public List<String> nextRecord() throws IOException
	{
		// 最初の読み込み
		int tokenType = readNextToken();
		if (tokenType == TT_EOF) {
			// ストリーム終端に到達
			_recordBeginIndex = getTokenBeginIndex();
			_recordEndIndex   = getTokenEndIndex();
			_recordString     = "";
			return null;
		}
		
		/*=== for Debug : エスケープ文字が奇数かどうかをチェック ===/
		{
			int cntQuot = 0;
			int pos = 0;
			while ((pos = getStringBuffer().indexOf("\"", pos)) >= 0) {
				cntQuot++;
				pos++;
			}
			if ((cntQuot % 2) != 0) {
				// QUOT が奇数？
				String strTest = getStringBuffer().toString();
				System.err.println("[Warning(line:" + (getRecordCount()+1) + ")] (CsvFileTokenier#nextRecord()) Quote count is odd in CSV Record string\n[" + strTest + "]");
			}
		}
		/*=== end of Debug ===*/
		
		// トークン解析
		CsvFieldDecoder csvdecoder = getCsvFieldDecoder();
		long recBeginIndex = getTokenBeginIndex();
		long recEndIndex   = getTokenEndIndex();
		int recBytes       = getTokenByteSize();
		// デコード
		int state = csvdecoder.decode();
		for (; state == CsvFieldDecoder.DS_FIELD_ENQUOTE; ) {
			// 次のトークンを読み込む
			tokenType = readNextToken();
			if (tokenType == TT_EOF) {
				// ストリーム終端
				break;
			}
			if (csvdecoder.getCsvFormat().isAllowMutiLineField() || tokenType != TT_EOL) {
				recEndIndex = getTokenEndIndex();
				recBytes += getTokenByteSize();
			}
			state = csvdecoder.decode();
		}
		// デコード完了
		csvdecoder.flush();
		List<String> fieldList = csvdecoder.fieldList();
		int recChars = csvdecoder.position();
		String recString = csvdecoder.getRecordString();
		csvdecoder.reset(true);
		// 必要なら、次の改行を読み飛ばす
		if (isEolSignificant() && tokenType!=TT_EOL) {
			tokenType = readNextToken();
			if (tokenType == TT_TEXT) {
				// check
				throw new IOException("Need EOL or EOF, but read text line token.");
			}
		}
		clearTokenText();
		
		// トークン情報の保存
		_recordBeginIndex = recBeginIndex;
		_recordEndIndex   = recEndIndex;
		if (_maxRecordBytes < recBytes)
			_maxRecordBytes = recBytes;
		if (_maxRecordChars < recChars)
			_maxRecordChars = recChars;
		if (_maxRecordFields < fieldList.size())
			_maxRecordFields = fieldList.size();
		++_recordCount;
		_recordString = recString;
		return fieldList;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setup() {
		super.setEolSignificant(true);	// 常に EOL もトークンとして読み込む
		this._recordBeginIndex = 0L;
		this._recordEndIndex   = 0L;
		this._recordCount      = 0L;
		this._maxRecordBytes   = 0;
		this._maxRecordChars   = 0;
		this._maxRecordFields  = 0;
		this._recordString     = "";
	}
	
	protected CsvFieldDecoder createCsvFieldDecoder() {
		CsvFieldDecoder newDecoder = new CsvFieldDecoder(getStringBuffer());
		return newDecoder;
	}
	
	protected CsvFieldDecoder getCsvFieldDecoder() {
		if (_decoder == null) {
			_decoder = createCsvFieldDecoder();
		}
		return _decoder;
	}
	
	@Override
	protected CsvTokenPosition createNextReadPosition() {
		CsvTokenPosition pos = new CsvTokenPosition();
		setNextReadPosition(pos);
		return pos;
	}

	@Override
	protected CsvTokenPosition createCurrentTokenPosition() {
		CsvTokenPosition pos = new CsvTokenPosition();
		setCurrentTokenPosition(pos);
		return pos;
	}

	@Override
	protected void setNextReadPosition(TextTokenPosition position) {
		super.setNextReadPosition(position);
		if (position instanceof CsvTokenPosition) {
			CsvTokenPosition pos = (CsvTokenPosition)position;
			pos.setLastRecordCount(_recordCount);
			pos.setLastMaxFieldCount(_maxRecordFields);
		}
	}

	@Override
	protected void setCurrentTokenPosition(TextTokenPosition position) {
		super.setCurrentTokenPosition(position);
		if (position instanceof CsvTokenPosition) {
			CsvTokenPosition pos = (CsvTokenPosition)position;
			long reccnt = _recordCount - 1L;
			if (reccnt > 0) {
				pos.setLastRecordCount(reccnt);
				pos.setLastMaxFieldCount(_maxRecordFields);
			} else {
				pos.setLastRecordCount(0L);
				pos.setLastMaxFieldCount(0);
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static protected class CsvTokenPosition extends TextTokenPosition
	{
		protected long	_lastRecordCount;
		protected int	_lastMaxFields;
		
		public CsvTokenPosition() {}
		
		public long getLastRecordCount() {
			return _lastRecordCount;
		}
		
		public int getLastMaxFieldCount() {
			return _lastMaxFields;
		}
		
		public void setLastRecordCount(long count) {
			_lastRecordCount = count;
		}
		
		public void setLastMaxFieldCount(int count) {
			_lastMaxFields = count;
		}
	}
}
