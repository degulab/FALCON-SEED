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
 * @(#)CsvFormat.java	1.90	2013/08/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.csv.internal;

/**
 * CSVフォーマットの定義情報を保持するクラス。<br>
 * このクラスは、フィールド区切り文字、クオート文字、クオートエスケープの有効無効の指定を
 * 保持する。
 * <p>
 * このクラスインスタンス生成直後では、フィールド区切り文字はカンマ(,)、
 * クオート文字はダブルクオーテーション(&quot;)、複数行フィールドを許可、クオートによるエスケープは有効となるように
 * 設定されている。
 * 
 * 
 * @version 1.90	2013/08/02
 * @since 1.90
 */
public class CsvFormat implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** CSV(Comma Separated Values) のフィールド区切り文字 **/
	static public final char CSV_DELIMITER_CHAR = ',';
	/** TSV(Tab Separated Values) のフィールド区切り文字 **/
	static public final char TSV_DELIMITER_CHAR = '\t';
	/** SSV(Space Separated Values) のフィールド区切り文字 **/
	static public final char SSV_DELIMITER_CHAR = ' ';
	/** CSV(Character Separated Values) の標準フィールド区切り文字 **/
	static public final char DEFAULT_DELIMITER_CHAR = CSV_DELIMITER_CHAR;
	/** CSV(Character Separated Values) の標準クオート文字 **/
	static public final char DEFAULT_QUOTE_CHAR = '\"';
	/** CSV(Character Separated Values) の標準レコード区切り文字 **/
	static public final String DEFAULT_RECORD_DELIMITER = "\r\n";
	/** 改行文字(CR) **/
	static public final char CR = '\r';
	/** 改行文字(LF) **/
	static public final char LF = '\n';

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ヘッダレコード数 **/
	protected long		_numHeaderRecords;
	/** フィールド区切り文字 **/
	protected char		_delimiter;
	/** クオート文字 **/
	protected char		_quote;
	/** 複数行フィールドを許可するフラグ **/
	protected boolean	_allowMultiLineField;
	/** クオートによるレコード区切りやフィールド区切りのエスケープが有効であることを示すフラグ **/
	protected boolean	_enableQuoteEscape;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準的な <code>CSV(Comma Separated Values)</code> フォーマットで、
	 * 新しいインスタンスを生成する。
	 */
	public CsvFormat() {
		_numHeaderRecords    = 0L;
		_delimiter           = DEFAULT_DELIMITER_CHAR;
		_quote               = DEFAULT_QUOTE_CHAR;
		_allowMultiLineField = true;
		_enableQuoteEscape   = true;
	}

	/**
	 * 標準的な <code>CSV(Comma Separated Values)</code> フォーマットをもとに、
	 * 指定されたフィールド区切り文字で、新しいインスタンスを生成する。
	 * @param delimiter	フィールド区切り文字
	 * @throws IllegalArgumentException	フィールド区切り文字が、クオート文字やレコード区切り文字('\r' または '\n')と競合する場合
	 */
	public CsvFormat(char delimiter) {
		this(delimiter, DEFAULT_QUOTE_CHAR);
	}

	/**
	 * 標準的な <code>CSV(Comma Separated Values)</code> フォーマットをもとに、
	 * 指定されたフィールド区切り文字とクオート文字で、新しいインスタンスを生成する。
	 * @param delimiter	フィールド区切り文字
	 * @param quote		クオート文字
	 * @throws IllegalArgumentException	フィールド区切り文字とクオート文字が競合する場合、もしくはレコード区切り文字('\r' または '\n')と競合する場合
	 */
	public CsvFormat(char delimiter, char quote) {
		// check
		validDelimiterAndQuoteCharacterArgument(delimiter, quote);
		
		// set
		_numHeaderRecords    = 0L;
		_delimiter           = delimiter;
		_quote               = quote;
		_allowMultiLineField = true;
		_enableQuoteEscape   = true;
	}

	/**
	 * 指定されたフォーマットと同じ内容の、新しいインスタンスを生成する。
	 * @param src	コピー元のオブジェクト
	 */
	public CsvFormat(final CsvFormat src) {
		_numHeaderRecords    = src._numHeaderRecords;
		_delimiter           = src._delimiter;
		_quote               = src._quote;
		_allowMultiLineField = src._allowMultiLineField;
		_enableQuoteEscape   = src._enableQuoteEscape;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在設定されているヘッダレコード数を返す。設定されてない場合は 0 を返す。
	 */
	public long getHeaderRecordCount() {
		return _numHeaderRecords;
	}

	/**
	 * ヘッダレコード数を設定する。
	 * <em>numRecords</em> に 0 以下の値を指定した場合、ヘッダレコード数は 0 となる。
	 * @param numRecords	ヘッダレコード数
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setHeaderRecordCount(long numRecords) {
		if (numRecords < 0L)
			numRecords = 0L;
		if (numRecords != _numHeaderRecords) {
			_numHeaderRecords = numRecords;
			return true;
		}
		return false;
	}

	/**
	 * デコードにおいて、クオートによるエスケープ処理が有効かどうかを取得する。
	 * デフォルトでは、クオートによるエスケープ処理は有効に設定されている。
	 * @return	クオートによるエスケープ処理が有効であれば <tt>true</tt>、
	 * 			無効であれば <tt>false</tt> を返す。
	 */
	public boolean isQuoteEscapeEnabled() {
		return _enableQuoteEscape;
	}

	/**
	 * クオートによるエスケープ処理を有効もしくは無効に設定する。
	 * @param toEnable	クオートによるエスケープ処理を有効とする場合は <tt>true</tt>、
	 * 					無効とする場合は <tt>false</tt> を指定する。
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setQuoteEscapeEnabled(boolean toEnable) {
		if (toEnable != _enableQuoteEscape) {
			_enableQuoteEscape = toEnable;
			return true;
		}
		return false;
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
		return _allowMultiLineField;
	}

	/**
	 * 複数行フィールドを許可もしくは禁止する。
	 * @param toAllow	複数行フィールドを許可する場合は <tt>true</tt>、
	 * 					禁止する場合は <tt>false</tt> を返す。
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setAllowMultiLineField(boolean toAllow) {
		if (toAllow != _allowMultiLineField) {
			_allowMultiLineField = toAllow;
			return true;
		}
		return false;
	}

	/**
	 * 現在のフィールド区切り文字を取得する。
	 * デフォルトでは、カンマ(,)に設定されている。
	 * @return	現在のフィールド区切り文字
	 */
	public char getDelimiterChar() {
		return _delimiter;
	}

	/**
	 * フィールド区切り文字を設定する。
	 * 設定されているクオート文字と同じ文字や、レコード終端文字(改行文字)は指定できない。
	 * @param delimiter	新しいフィールド区切り文字
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalArgumentException	フィールド区切り文字が、クオート文字やレコード区切り文字('\r' または '\n')と競合する場合
	 */
	public boolean setDelimiterChar(char delimiter) {
		if (delimiter != _delimiter) {
			validDelimiterAndQuoteCharacterArgument(delimiter, _quote);
			_delimiter = delimiter;
			return true;
		}
		return false;
	}

	/**
	 * 現在のクオート文字を取得する。
	 * デフォルトでは、ダブルクオーテーション(&quot;)に設定されている。
	 * @return	現在のクオート文字
	 */
	public char getQuoteChar() {
		return _quote;
	}

	/**
	 * クオート文字を設定する。
	 * 設定されているフィールド区切り文字と同じ文字や、レコード終端文字(改行文字)は指定できない。
	 * @param quote	新しいクオート文字
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalArgumentException	クオート文字が、フィールド区切り文字やレコード区切り文字('\r' または '\n')と競合する場合
	 */
	public boolean setQuoteChar(char quote) {
		if (quote != _quote) {
			validDelimiterAndQuoteCharacterArgument(_delimiter, quote);
			_quote = quote;
			return true;
		}
		return false;
	}

	/**
	 * このオブジェクトの設定を、指定されたフォーマットで更新する。
	 * @param src	コピー元のオブジェクト
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean setFormat(final CsvFormat src) {
		if (!isEqualParameters(src)) {
			_numHeaderRecords    = src._numHeaderRecords;
			_delimiter           = src._delimiter;
			_quote               = src._quote;
			_allowMultiLineField = src._allowMultiLineField;
			_enableQuoteEscape   = src._enableQuoteEscape;
			
			return true;
		}
		return false;
	}

	@Override
	public CsvFormat clone() {
		try {
			CsvFormat newFormat = (CsvFormat)super.clone();
			return newFormat;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		int result = 1;
		//--- header records
        result = 31 * result + Long.valueOf(_numHeaderRecords).hashCode();
		//--- delimiter
		result = 31 * result + _delimiter;
		//--- quote
		result = 31 * result + _quote;
		//--- allowMultiLineField
        result = 31 * result + (_allowMultiLineField ? 1231 : 1237);
		//--- enableQuoteEscape
        result = 31 * result + (_enableQuoteEscape ? 1231 : 1237);
        //---
        return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj != null && obj.getClass() == CsvFormat.class) {
			return isEqualParameters((CsvFormat)obj);
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("[headerRecordCount(");
		sb.append(_numHeaderRecords);
		sb.append("), delimiter(");
		sb.append(formatCharacter(_delimiter));
		sb.append("), quote(");
		sb.append(formatCharacter(_quote));
		sb.append("), Quote escape(");
		sb.append(_enableQuoteEscape);
		sb.append("), Allow multi-line field(");
		sb.append(_allowMultiLineField);
		sb.append(")]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void validDelimiterAndQuoteCharacterArgument(char delimiter, char quote) {
		if (delimiter == quote) {
			String errmsg = String.format("Delimiter character([%1$c]=0x%1$04X) equals Quote character([%2$c]=0x%2$04X)", delimiter, quote);
			throw new IllegalArgumentException(errmsg);
		}
		if (CR == delimiter || LF == delimiter) {
			String errmsg = String.format("Delimiter character([%1$c]=0x%1$04X) is Record delimiter(CR or LF).", delimiter);
			throw new IllegalArgumentException(errmsg);
		}
		if (CR == quote || LF == quote) {
			String errmsg = String.format("Quote character([%1$c]=0x%1$04X) is Record delimiter(CR or LF).", quote);
			throw new IllegalArgumentException(errmsg);
		}
	}
	
	protected String formatCharacter(char c) {
		return String.format("[%1$c]=0x%1$04X", c);
	}
	
	private boolean isEqualParameters(final CsvFormat aFormat) {
		if (this._numHeaderRecords != aFormat._numHeaderRecords)
			return false;
		
		if (this._delimiter != aFormat._delimiter)
			return false;
		
		if (this._quote != aFormat._quote)
			return false;
		
		if (this._allowMultiLineField != aFormat._allowMultiLineField)
			return false;
		
		if (this._enableQuoteEscape != aFormat._enableQuoteEscape)
			return false;

		return true;
	}
}
