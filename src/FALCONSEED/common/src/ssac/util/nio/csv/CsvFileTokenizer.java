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
 * @(#)CsvFileTokenizer.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileTokenizer.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import ssac.util.nio.TextFileLineTokenizerBase;

/**
 * CSV形式のテキストファイルを、ファイル先頭からトークン分割するクラス。
 * 
 * @version 1.17	2010/11/19
 * @since 1.16
 */
public class CsvFileTokenizer extends TextFileLineTokenizerBase
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
	private long			_recordBeginIndex;
	/** 読み込み済みレコード終端のファイルインデックス **/
	private long			_recordEndIndex;
	/** 現在までのレコード数 **/
	private long			_recordCount;
	/** 現在までのレコード最大バイト数 **/
	private int			_maxRecordBytes;
	/** 現在までのレコード最大文字数 **/
	private int			_maxRecordChars;
	/** 現在までのレコード最大フィールド数 **/
	private int			_maxRecordFields;
	/** 読み込み済みレコードのフィールド分割前の文字列 **/
	private String			_recordString;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public CsvFileTokenizer(File file) throws FileNotFoundException
	{
		super(file);
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
	 * クオートによるエスケープ処理が有効かどうかを取得する。
	 * デフォルトでは、クオートによるエスケープ処理は有効に設定されている。
	 * @return	クオートによるエスケープ処理が有効であれば <tt>true</tt>、
	 * 			無効であれば <tt>false</tt> を返す。
	 */
	public boolean isQuoteEscapeEnabled() {
		return getCsvFieldDecoder().isQuoteEscapeEnabled();
	}

	/**
	 * クオートによるエスケープ処理を有効もしくは無効に設定する。
	 * @param toEnable	クオートによるエスケープ処理を有効とする場合は <tt>true</tt>、
	 * 					無効とする場合は <tt>false</tt> を指定する。
	 */
	public void setQuoteEscapeEnabled(boolean toEnable) {
		getCsvFieldDecoder().setQuoteEscapeEnabled(toEnable);
	}

	/**
	 * 複数行フィールドが許可されているかどうかを取得する。
	 * デフォルトでは、複数行フィールドは許可されている。
	 * <p>
	 * 複数行フィールドは、改行文字を含むフィールドであり、{@link #isQuoteEscapeEnabled()} が
	 * <tt>true</tt> を返す場合のみ、複数行フィールドとしてデコードされる。
	 * @return	複数行フィールドが許可されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isAllowMutiLineField() {
		return getCsvFieldDecoder().isAllowMutiLineField();
	}

	/**
	 * 複数行フィールドを許可もしくは禁止する。
	 * @param toAllow	複数行フィールドを許可する場合は <tt>true</tt>、
	 * 					禁止する場合は <tt>false</tt> を返す。
	 */
	public void setAllowMultiLineField(boolean toAllow) {
		getCsvFieldDecoder().setAllowMultiLineField(toAllow);
	}
	
	/**
	 * 現在のフィールド区切り文字を取得する。
	 * デフォルトでは、カンマ(,)に設定されている。
	 * @return	現在のフィールド区切り文字
	 */
	public char getDelimiterChar() {
		return getCsvFieldDecoder().getDelimiterChar();
	}
	
	/**
	 * フィールド区切り文字を設定する。
	 * 設定されているクオート文字と同じ文字や、レコード終端文字(改行文字)は指定できない。
	 * @param newDelim	新しいフィールド区切り文字
	 * @throws IllegalArgumentException	<em>newDelim</em> に指定された文字が、設定されているクオート文字と同じ場合、
	 * 										もしくは改行文字('\r' または '\n')の場合
	 */
	public void setDelimiterChar(char newDelim) {
		getCsvFieldDecoder().setDelimiterChar(newDelim);
	}
	
	/**
	 * 現在のクオート文字を取得する。
	 * デフォルトでは、ダブルクオーテーション(&quot;)に設定されている。
	 * @return	現在のクオート文字
	 */
	public char getQuoteChar() {
		return getCsvFieldDecoder().getQuoteChar();
	}
	
	/**
	 * クオート文字を設定する。
	 * 設定されているフィールド区切り文字と同じ文字や、レコード終端文字(改行文字)は指定できない。
	 * @param newQuote	新しいクオート文字
	 * @throws IllegalArgumentException	<em>newQuote</em> に指定された文字が、設定されているフィールド区切り文字と同じ場合、
	 * 										もしくは改行文字('\r' または '\n')の場合
	 */
	public void setQuoteChar(char newQuote) {
		getCsvFieldDecoder().setQuoteChar(newQuote);
	}
	
	/**
	 * このオブジェクトのCSVパラメータを、指定されたCSVパラメータに設定する。
	 * @param newParams		CSVパラメータ
	 */
	public void setCsvParameters(final CsvParameters newParams) {
		getCsvFieldDecoder().setCsvParameters(newParams);
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
	 * 次の CSV レコードを読み込む。
	 * @return	読み込まれた CSV レコードに含まれるフィールドの配列を返す。
	 * 			ストリームの終端に到達している場合は <tt>null</tt> を返す。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public String[] nextRecord() throws IOException
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
			if (csvdecoder.isAllowMutiLineField() || tokenType != TT_EOL) {
				recEndIndex = getTokenEndIndex();
				recBytes += getTokenByteSize();
			}
			state = csvdecoder.decode();
		}
		// デコード完了
		csvdecoder.flush();
		String[] fields = csvdecoder.fields();
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
		if (_maxRecordFields < fields.length)
			_maxRecordFields = fields.length;
		++_recordCount;
		_recordString = recString;
		return fields;
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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
