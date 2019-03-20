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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvBufferedWriter.java	3.3.0	2016/05/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvBufferedWriter.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import ssac.util.nio.ICloseable;

/**
 * CSVレコードを出力するライタークラス。このクラスは、文字列をバッファリングし、指定されたストリームへ出力する。
 * 出力の際、CSVフィールドの文字列は適切にエンコードされ出力される。
 * <p>
 * CSVフィールドのエンコードでは、ユーザーによって設定されたクオート文字とフィールド区切り文字を利用する。<br>
 * フィールド区切り文字は {@link #setDelimiterChar(char)} メソッドによって、
 * クオート文字は {@link #setQuoteChar(char)} メソッドによって設定できる。<br>
 * また、クオートによるエスケープを有効とするか無効とするかも設定できる。クオートによる
 * エスケープとは、クオート文字から次のクオート文字が出現するまでフィールド区切り文字や
 * レコード終端文字をフィールド内の文字として扱う。クオートによるエスケープでは、
 * クオート文字が2つ連続している場合はクオート文字そのものをフィールド文字1文字分として扱う。
 * <p>
 * このクラスインスタンス生成直後では、フィールド区切り文字はカンマ(,)、
 * クオート文字はダブルクオーテーション(&quot;)、クオートによるエスケープは有効となるように
 * 設定されている。
 * <p><b>注：</b>
 * <blockquote>
 * このクラスは同期化されていない。
 * </blockquote>
 * 
 * @version 3.3.0
 * @since 1.17
 */
public class CsvBufferedWriter implements ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** CSVパラメータ **/
	private final CsvParameters	_csvParams = new CsvParameters();

	/** ライター **/
	protected BufferedWriter		_writer;
	/** 書き込み済みのレコード数 **/
	protected long		_wroteRecords = 0L;
	/** 書き込み済みのフィールド数 **/
	protected int		_wroteFields  = 0;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvBufferedWriter(Writer out) {
		this._writer = new BufferedWriter(out);
	}
	
	public CsvBufferedWriter(Writer out, int bufferSize) {
		this._writer = new BufferedWriter(out, bufferSize);
	}
	
	public CsvBufferedWriter(Writer out, final CsvParameters csvParams) {
		this(out);
		if (csvParams != null) {
			setCsvParameters(csvParams);
		}
	}
	
	public CsvBufferedWriter(Writer out, int bufferSize, final CsvParameters csvParams) {
		this(out, bufferSize);
		if (csvParams != null) {
			setCsvParameters(csvParams);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * デコードにおいて、クオートによるエスケープ処理が有効かどうかを取得する。
	 * デフォルトでは、クオートによるエスケープ処理は有効に設定されている。
	 * @return	クオートによるエスケープ処理が有効であれば <tt>true</tt>、
	 * 			無効であれば <tt>false</tt> を返す。
	 */
	public boolean isQuoteEscapeEnabled() {
		return _csvParams.isQuoteEscapeEnabled();
	}

	/**
	 * クオートによるエスケープ処理を有効もしくは無効に設定する。
	 * @param toEnable	クオートによるエスケープ処理を有効とする場合は <tt>true</tt>、
	 * 					無効とする場合は <tt>false</tt> を指定する。
	 */
	public void setQuoteEscapeEnabled(boolean toEnable) {
		_csvParams.setQuoteEscapeEnabled(toEnable);
	}

	/**
	 * デコードにおいて、複数行フィールドが許可されているかどうかを取得する。
	 * デフォルトでは、複数行フィールドは許可されている。
	 * <p>
	 * 複数行フィールドは、改行文字を含むフィールドであり、{@link #isQuoteEscapeEnabled()} が
	 * <tt>true</tt> を返す場合のみ、複数行フィールドとしてデコードされる。
	 * @return	複数行フィールドが許可されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isAllowMutiLineField() {
		return _csvParams.isAllowMutiLineField();
	}

	/**
	 * 複数行フィールドを許可もしくは禁止する。
	 * @param toAllow	複数行フィールドを許可する場合は <tt>true</tt>、
	 * 					禁止する場合は <tt>false</tt> を返す。
	 */
	public void setAllowMultiLineField(boolean toAllow) {
		this._csvParams.setAllowMultiLineField(toAllow);
	}

	/**
	 * 現在のフィールド区切り文字を取得する。
	 * デフォルトでは、カンマ(,)に設定されている。
	 * @return	現在のフィールド区切り文字
	 */
	public char getDelimiterChar() {
		return _csvParams.getDelimiterChar();
	}

	/**
	 * フィールド区切り文字を設定する。
	 * 設定されているクオート文字と同じ文字や、レコード終端文字(改行文字)は指定できない。
	 * @param newDelim	新しいフィールド区切り文字
	 * @throws IllegalArgumentException	<em>newDelim</em> に指定された文字が、設定されているクオート文字と同じ場合、
	 * 										もしくは改行文字('\r' または '\n')の場合
	 */
	public void setDelimiterChar(char newDelim) {
		_csvParams.setDelimiterChar(newDelim);
	}

	/**
	 * 現在のクオート文字を取得する。
	 * デフォルトでは、ダブルクオーテーション(&quot;)に設定されている。
	 * @return	現在のクオート文字
	 */
	public char getQuoteChar() {
		return _csvParams.getQuoteChar();
	}

	/**
	 * クオート文字を設定する。
	 * 設定されているフィールド区切り文字と同じ文字や、レコード終端文字(改行文字)は指定できない。
	 * @param newQuote	新しいクオート文字
	 * @throws IllegalArgumentException	<em>newQuote</em> に指定された文字が、設定されているフィールド区切り文字と同じ場合、
	 * 										もしくは改行文字('\r' または '\n')の場合
	 */
	public void setQuoteChar(char newQuote) {
		_csvParams.setQuoteChar(newQuote);
	}
	
	/**
	 * このオブジェクトのCSVパラメータを、指定されたCSVパラメータに設定する。
	 * @param newParams		CSVパラメータ
	 */
	public void setCsvParameters(final CsvParameters newParams) {
		_csvParams.setParameters(newParams);
	}

	/**
	 * このクラスのメソッドによって出力されたレコード数を返す。
	 * ただし、バッファリングされるため、実際にストリームへ出力されたレコード数とは限らない。
	 * @return	出力済みのレコード数
	 */
	public long getWroteRecordCount() {
		return _wroteRecords;
	}

	/**
	 * 現在出力中のレコードにおいて、このクラスのメソッドによって出力されたフィールド数を返す。
	 * ただし、バッファリングされるため、実際にストリームへ出力されたフィールド数とは限らない。
	 * @return	出力済みのフィールド数
	 */
	public int getWroteFieldCount() {
		return _wroteFields;
	}

	/**
	 * 指定された文字列を新しいフィールド値として出力する。
	 * @param fieldValue	フィールド値として出力する文字列
	 * @throws IOException	入出力エラーが発生した場合、もしくはストリームが閉じられている場合
	 */
	public void addField(String fieldValue)
	throws IOException
	{
		ensureOpen();
		CsvParameters csvParams = getCsvParameters();
		if (fieldValue != null) {
			// CSV出力設定に従い、フィールド値を出力する
			if (!csvParams.isQuoteEscapeEnabled()) {
				//--- quote 文字によるエスケープを行わない
				writeFieldWithoutEnquote(fieldValue, fieldValue.length(), csvParams.getDelimiterChar());
			}
			else if (!csvParams.isAllowMutiLineField()) {
				//--- quote 文字によるエスケープは行うが、改行文字は強制的にレコード区切りとする
				writeFieldEnquoteSingleLine(fieldValue, fieldValue.length(), csvParams.getDelimiterChar(), csvParams.getQuoteChar());
			}
			else {
				//--- quote 文字によるエスケープを行う(改行文字もエスケープ)
				writeFieldEnquoteMultiLine(fieldValue, fieldValue.length(), csvParams.getDelimiterChar(), csvParams.getQuoteChar());
			}
		} else {
			// 空のフィールドを出力する
			writeFieldDelimiterAsNeeded(csvParams.getDelimiterChar());
		}
	}

	/**
	 * 指定されたリストのすべての要素を新しいフィールド値として出力する。
	 * 指定された引数が <tt>null</tt> もしくは空の場合、このメソッドは何もしない。
	 * @param fieldValues	フィールド値のリスト
	 * @throws IOException	入出力エラーが発生した場合、もしくはストリームが閉じられている場合
	 * @since 3.3.0
	 */
	public void addAllFields(List<String> fieldValues)
	throws IOException
	{
		ensureOpen();
		if (fieldValues != null && !fieldValues.isEmpty()) {
			CsvParameters csvParams = getCsvParameters();
			char cDelim = csvParams.getDelimiterChar();
			char cQuote = csvParams.getQuoteChar();
			// CSV出力設定に従い、フィールド値を出力する
			if (!csvParams.isQuoteEscapeEnabled()) {
				//--- quote 文字によるエスケープを行わない
				for (String strValue : fieldValues) {
					if (strValue != null) {
						writeFieldWithoutEnquote(strValue, strValue.length(), cDelim);
					} else {
						//--- 空のフィールドを出力
						writeFieldDelimiterAsNeeded(cDelim);
					}
				}
			}
			else if (!csvParams.isAllowMutiLineField()) {
				//--- quote 文字によるエスケープは行うが、改行文字は強制的にレコード区切りとする
				for (String strValue : fieldValues) {
					if (strValue != null) {
						writeFieldEnquoteSingleLine(strValue, strValue.length(), cDelim, cQuote);
					} else {
						//--- 空のフィールドを出力
						writeFieldDelimiterAsNeeded(cDelim);
					}
				}
			}
			else {
				//--- quote 文字によるエスケープを行う(改行文字もエスケープ)
				for (String strValue : fieldValues) {
					if (strValue != null) {
						writeFieldEnquoteMultiLine(strValue, strValue.length(), cDelim, cQuote);
					} else {
						//--- 空のフィールドを出力
						writeFieldDelimiterAsNeeded(cDelim);
					}
				}
			}
		}
	}
	
	/**
	 * 指定された配列のすべての要素を新しいフィールド値として出力する。
	 * 指定された引数が <tt>null</tt> もしくは要素が空の場合、このメソッドは何もしない。
	 * @param fieldValues	フィールド値の配列
	 * @throws IOException	入出力エラーが発生した場合、もしくはストリームが閉じられている場合
	 */
	public void addAllFields(String...fieldValues)
	throws IOException
	{
		ensureOpen();
		if (fieldValues != null && fieldValues.length > 0) {
			CsvParameters csvParams = getCsvParameters();
			char cDelim = csvParams.getDelimiterChar();
			char cQuote = csvParams.getQuoteChar();
			// CSV出力設定に従い、フィールド値を出力する
			if (!csvParams.isQuoteEscapeEnabled()) {
				//--- quote 文字によるエスケープを行わない
				for (String strValue : fieldValues) {
					if (strValue != null) {
						writeFieldWithoutEnquote(strValue, strValue.length(), cDelim);
					} else {
						//--- 空のフィールドを出力
						writeFieldDelimiterAsNeeded(cDelim);
					}
				}
			}
			else if (!csvParams.isAllowMutiLineField()) {
				//--- quote 文字によるエスケープは行うが、改行文字は強制的にレコード区切りとする
				for (String strValue : fieldValues) {
					if (strValue != null) {
						writeFieldEnquoteSingleLine(strValue, strValue.length(), cDelim, cQuote);
					} else {
						//--- 空のフィールドを出力
						writeFieldDelimiterAsNeeded(cDelim);
					}
				}
			}
			else {
				//--- quote 文字によるエスケープを行う(改行文字もエスケープ)
				for (String strValue : fieldValues) {
					if (strValue != null) {
						writeFieldEnquoteMultiLine(strValue, strValue.length(), cDelim, cQuote);
					} else {
						//--- 空のフィールドを出力
						writeFieldDelimiterAsNeeded(cDelim);
					}
				}
			}
		}
	}

	/**
	 * レコード区切り文字を出力し、新しいレコードを開始する。
	 * @throws IOException	入出力エラーが発生した場合、もしくはストリームが閉じられている場合
	 */
	public void newRecord() throws IOException
	{
		ensureOpen();
		writeRecordDelimiter();
	}

	/**
	 * レコード区切り文字を出力し、新しいレコードを開始する。
	 * 現在出力中のレコードのフィールド数が指定されたフィールド数より小さい場合は、
	 * 指定されたフィールド数となるよう空のフィールドを出力してからレコード区切り文字を出力する。
	 * @param minFields		最低限出力するフィールド数。出力済みフィールド数以下の場合は無視される。
	 * @throws IOException	入出力エラーが発生した場合、もしくはストリームが閉じられている場合
	 */
	public void newRecordEnsureFieldCount(int minFields) throws IOException
	{
		ensureOpen();
		char cDelim = getCsvParameters().getDelimiterChar();
		int wroteFlds = _wroteFields;
		if (wroteFlds < minFields) {
			if (wroteFlds > 0) {
				do {
					_writer.write(cDelim);
					wroteFlds++;
				} while (wroteFlds < minFields);
			} else {
				_writer.write(cDelim);
				wroteFlds++;
				while (wroteFlds < minFields) {
					_writer.write(cDelim);
					wroteFlds++;
				}
			}
		}

		writeRecordDelimiter();
	}

	/**
	 * 現在のバッファの状態をストリームへ出力する。
	 * @throws IOException	入出力エラーが発生した場合、もしくはストリームが閉じられている場合
	 */
	public void flush() throws IOException
	{
		ensureOpen();
		_writer.flush();
	}
	
	public boolean isOpen() {
		return (_writer != null);
	}

	/**
	 * ストリームを閉じる。すでに閉じている場合は、何もしない。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void close() throws IOException
	{
		if (_writer != null) {
			_writer.close();
			_writer = null;
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このクラスが保持する <code>CsvParameters</code> オブジェクトを返す。
	 */
	protected CsvParameters getCsvParameters() {
		return _csvParams;
	}

	/**
	 * ストリームがオープンされているかを取得する。
	 * このメソッドでは、本当にストリームがオープンされているかは不明
	 * @throws IOException	このクラスが保持するライターオブジェクトのインスタンスが存在しない場合
	 */
	protected void ensureOpen() throws IOException {
		if (_writer == null)
			throw new IOException("Stream closed");
	}

	/**
	 * 改行文字も含め、クオート文字によるエスケープを行い、指定された文字列を新しいCSVフィールドとして出力する。
	 * @param value		出力する文字列
	 * @param len		文字列の長さ
	 * @param cDelim	フィールド区切り文字
	 * @param cQuote	クオート文字
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void writeFieldEnquoteMultiLine(final String value, final int len, final char cDelim, final char cQuote)
	throws IOException
	{
		// 新しいフィールドの開始
		writeFieldDelimiterAsNeeded(cDelim);

		boolean enquoted = false;
		char cs = '\0';
		int spos = 0;
		int pos = 0;
		int clen;
		//--- enquote の判定
		for ( ; pos < len; ) {
			cs = value.charAt(pos);

			if (cQuote == cs || cDelim == cs || '\r' == cs || '\n' == cs) {
				//--- enquote 開始
				enquoted = true;
				_writer.write(cQuote);
				break;
			}
			
			//--- 次の文字へ
			pos++;
		}
		//--- enquote 判定後の処理
		if (enquoted) {
			//--- enquote 開始後
			for ( ; pos < len; ) {
				cs = value.charAt(pos);
				
				if (cQuote == cs) {
					//--- quote を含むフィールド値出力
					clen = pos - spos + 1;
					_writer.write(value, spos, clen);	// 必ず 1 文字以上出力
					//--- quote の二重化
					_writer.write(cQuote);
					//--- 次の位置へ
					pos++;
					spos = pos;
				}
				else {
					//--- 次の文字へ
					pos++;
				}
			}
			//--- enquote 完了
			clen = pos - spos;
			if (clen > 0) {
				//--- フィールド値あり
				_writer.write(value, spos, clen);
			}
			//--- quote を閉じる
			_writer.write(cQuote);
		} else {
			//--- enquote の必要なし
			clen = pos - spos;
			if (clen > 0) {
				//--- フィールド値あり
				_writer.write(value, spos, clen);
			}
		}
	}
	
	/**
	 * クオート文字によるエスケープを行い、指定された文字列を新しいCSVフィールドとして出力する。
	 * このメソッドでは改行文字は強制的にレコード区切り文字として出力し、エスケープの対象としない。
	 * @param value		出力する文字列
	 * @param len		文字列の長さ
	 * @param cDelim	フィールド区切り文字
	 * @param cQuote	クオート文字
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void writeFieldEnquoteSingleLine(final String value, final int len, final char cDelim, final char cQuote)
	throws IOException
	{
		// 新しいフィールドの開始
		writeFieldDelimiterAsNeeded(cDelim);
		
		boolean enquoted = false;
		char cs = '\0';
		int spos = 0;
		int pos = 0;
		int clen;
		do {
			//--- enquote の判定
			for ( ; pos < len; ) {
				cs = value.charAt(pos);

				if (cQuote == cs || cDelim == cs) {
					//--- enquote 開始
					enquoted = true;
					_writer.write(cQuote);
					break;	//--- enquote 処理へ
				}
				else if ('\r' == cs) {
					//--- フィールド値出力
					clen = pos - spos;
					if (clen > 0) {
						//--- フィールド値あり
						_writer.write(value, spos, clen);
					}
					//--- レコード終端出力
					writeRecordDelimiter();
					//--- 次の位置へ
					pos++;
					if (pos < len && '\n'==value.charAt(pos)) {
						pos++;
					}
					spos = pos;
					if (pos < len) {
						//--- 次のフィールドが存在するので、フィールド数をインクリメント
						_wroteFields++;
					}
				}
				else if ('\n' == cs) {
					//--- フィールド値出力
					clen = pos - spos;
					if (clen > 0) {
						//--- フィールド値あり
						_writer.write(value, spos, clen);
					}
					//--- レコード終端出力
					writeRecordDelimiter();
					//--- 次の位置へ
					pos++;
					spos = pos;
					if (pos < len) {
						//--- 次のフィールドが存在するので、フィールド数をインクリメント
						_wroteFields++;
					}
				}
				else {
					//--- 次の文字へ
					pos++;
				}
			}
			
			//--- enquote 判定後の処理
			if (enquoted) {
				//--- enquote 開始後
				for ( ; pos < len; ) {
					cs = value.charAt(pos);
					
					if (cQuote == cs) {
						//--- quote を含むフィールド値出力
						clen = pos - spos + 1;
						_writer.write(value, spos, clen);	// 必ず 1 文字以上出力
						//--- quote の二重化
						_writer.write(cQuote);
						//--- 次の位置へ
						pos++;
						spos = pos;
					}
					else if ('\r' == cs || '\n' == cs) {
						//--- レコード終端処理へ
						break;
					}
					else {
						//--- 次の文字へ
						pos++;
					}
				}
				//--- enquote 完了
				clen = pos - spos;
				if (clen > 0) {
					//--- フィールド値あり
					_writer.write(value, spos, clen);
				}
				//--- quote を閉じる
				_writer.write(cQuote);
				enquoted = false;
			} else {
				//--- enquote の必要なし
				clen = pos - spos;
				if (clen > 0) {
					//--- フィールド値あり
					_writer.write(value, spos, clen);
				}
			}
		} while (pos < len);
	}
	
	/**
	 * エスケープを行わずに、指定された文字列を新しいCSVフィールドとして出力する。
	 * フィールド区切り文字や改行文字が含まれている場合、フィールド区切り、レコード区切りとして適切に処理する。
	 * @param value		出力する文字列
	 * @param len		文字列の長さ
	 * @param cDelim	フィールド区切り文字
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void writeFieldWithoutEnquote(final String value, final int len, final char cDelim)
	throws IOException
	{
		// 新しいフィールドの開始
		writeFieldDelimiterAsNeeded(cDelim);
		
		char cs = '\0';
		int spos = 0;
		int pos = 0;
		int clen;
		for ( ; pos < len; ) {
			cs = value.charAt(pos);
			
			if (cDelim == cs) {
				//--- フィールド値出力
				clen = pos - spos;
				if (clen > 0) {
					//--- フィールド値あり
					_writer.write(value, spos, clen);
				}
				//--- フィールド区切り出力
				writeFieldDelimiterAsNeeded(cDelim);
				//--- 次の位置へ
				pos++;
				spos = pos;
			}
			else if ('\r' == cs) {
				//--- フィールド値出力
				clen = pos - spos;
				if (clen > 0) {
					//--- フィールド値あり
					_writer.write(value, spos, clen);
				}
				//--- レコード終端出力
				writeRecordDelimiter();
				//--- 次の位置へ
				pos++;
				if (pos < len && '\n'==value.charAt(pos)) {
					pos++;
				}
				spos = pos;
				if (pos < len) {
					//--- 次のフィールドが存在するので、フィールド数をインクリメント
					_wroteFields++;
				}
			}
			else if ('\n' == cs) {
				//--- フィールド値出力
				clen = pos - spos;
				if (clen > 0) {
					//--- フィールド値あり
					_writer.write(value, spos, clen);
				}
				//--- レコード終端出力
				writeRecordDelimiter();
				//--- 次の位置へ
				pos++;
				spos = pos;
				if (pos < len) {
					//--- 次のフィールドが存在するので、フィールド数をインクリメント
					_wroteFields++;
				}
			}
			else {
				//--- 次の文字へ
				pos++;
			}
		}

		//--- 未出力のフィールド値を出力
		clen = pos - spos;
		if (clen > 0) {
			//--- フィールド値あり
			_writer.write(value, spos, clen);
		}
	}

	/**
	 * 出力可能な場合にフィールド区切り文字を出力し、出力済みフィールド数をインクリメントする。
	 * 出力済みフィールド数が 0 の場合は先頭フィールドとみなし、フィールド区切り文字は出力しない。
	 * @param cDelim	フィールド区切り文字
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void writeFieldDelimiterAsNeeded(final char cDelim)
	throws IOException
	{
		if (_wroteFields > 0) {
			_writer.write(cDelim);
		}
		_wroteFields++;
	}

	/**
	 * レコード区切り文字とする改行文字を出力する。
	 * ここで出力される改行文字は、JAVAシステムプロパティに指定されている文字となる。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void writeRecordDelimiter()
	throws IOException
	{
		_writer.newLine();
		_wroteFields = 0;
		_wroteRecords++;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
