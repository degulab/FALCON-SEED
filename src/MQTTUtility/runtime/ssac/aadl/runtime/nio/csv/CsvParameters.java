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
 * @(#)CsvParameters.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvParameters.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.nio.csv;

import ssac.aadl.runtime.util.Validations;

/**
 * CSVフォーマットのパラメータを保持するクラス。<br>
 * このクラスは、フィールド区切り文字、クオート文字、クオートエスケープの有効無効の指定を
 * 保持する。
 * <p>
 * このクラスインスタンス生成直後では、フィールド区切り文字はカンマ(,)、
 * クオート文字はダブルクオーテーション(&quot;)、クオートによるエスケープは有効となるように
 * 設定されている。
 * 
 * 
 * @version 1.17	2010/11/19
 * @since 1.16
 */
public class CsvParameters implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ヘッダ行の有無 **/
	private boolean		_headerLine;
	/** データ型の自動判別を行うことを示すフラグ **/
	private boolean		_autoDetectDataType;
	/** フィールド区切り文字 **/
	private char			_delimiter;
	/** クオート文字 **/
	private char			_quote;
	/** 複数行フィールドを許可するフラグ **/
	private boolean		_allowMultiLineField;
	/** クオートによるレコード区切りやフィールド区切りのエスケープが有効であることを示すフラグ **/
	private boolean		_enableQuoteEscape;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvParameters() {
		this._headerLine = false;
		this._autoDetectDataType = true;
		this._delimiter = CsvUtil.CSV_DELIMITER_CHAR;
		this._quote     = CsvUtil.CSV_QUOTE_CHAR;
		this._allowMultiLineField = true;
		this._enableQuoteEscape = true;
	}
	
	public CsvParameters(final CsvParameters params) {
		setParameters(params);
	}
	
//	public CsvParameters(final CsvFileData filedata) {
//		setParameters(filedata.getCsvParameters());
//	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 第１行をヘッダ行として使用するかどうかを取得する。
	 * @return	第１行がヘッダ行に指定されている場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean getUseHeaderLine() {
		return _headerLine;
	}

	/**
	 * 第１行をヘッダ行として使用するかどうかを設定する。
	 * デフォルトでは、第１行は通常行となっている。
	 * @param toUse	第１行をヘッダ行とする場合は <tt>true</tt>、
	 * 				通常行とする場合は <tt>false</tt>
	 * @since 1.17
	 */
	public void setUseHeaderLine(boolean toUse) {
		this._headerLine = toUse;
	}

	/**
	 * 読み込み時にデータ型の自動判別を行うかどうかの設定を取得する。
	 * @return	データ型の自動判別を行う場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean getAutoDetectDataType() {
		return _autoDetectDataType;
	}

	/**
	 * 読み込み時にデータ型の自動判別を行うかどうかを設定する。
	 * データ型の自動判別は基本的に、数値、真偽値、文字列のどれかであることを
	 * 判別する機能となる。
	 * デフォルトでは、自動判別が有効となっている。
	 * @param flag	自動判別を有効とする場合は <tt>true</tt>、
	 * 				無効とする場合は <tt>false</tt>
	 * @since 1.17
	 */
	public void setAutoDetectDataType(boolean flag) {
		this._autoDetectDataType = flag;
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
	 */
	public void setQuoteEscapeEnabled(boolean toEnable) {
		this._enableQuoteEscape = toEnable;
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
	 */
	public void setAllowMultiLineField(boolean toAllow) {
		this._allowMultiLineField = toAllow;
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
	 * @param newDelim	新しいフィールド区切り文字
	 * @throws IllegalArgumentException	<em>newDelim</em> に指定された文字が、設定されているクオート文字と同じ場合、
	 * 										もしくは改行文字('\r' または '\n')の場合
	 */
	public void setDelimiterChar(char newDelim) {
		Validations.validArgument(newDelim!=_quote, "The specified Delimiter character same the Quote character : [%s]", String.valueOf(newDelim));
		Validations.validArgument(newDelim!=CsvUtil.CR, "Delimiter character cannot to use CR.");
		Validations.validArgument(newDelim!=CsvUtil.LF, "Delimiter character cannot to use LF.");
		this._delimiter = newDelim;
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
	 * @param newQuote	新しいクオート文字
	 * @throws IllegalArgumentException	<em>newQuote</em> に指定された文字が、設定されているフィールド区切り文字と同じ場合、
	 * 										もしくは改行文字('\r' または '\n')の場合
	 */
	public void setQuoteChar(char newQuote) {
		Validations.validArgument(newQuote!=_delimiter, "The specified Quote character same the Delimiter character : [%s]", String.valueOf(newQuote));
		Validations.validArgument(newQuote!=CsvUtil.CR, "Quote character cannot to use CR.");
		Validations.validArgument(newQuote!=CsvUtil.LF, "Quote character cannot to use LF.");
		this._quote = newQuote;
	}

	/**
	 * このオブジェクトのパラメータを、指定されたCSVパラメータに設定する。
	 * @param newParams		CSVパラメータ
	 */
	public void setParameters(final CsvParameters newParams) {
		this._headerLine = newParams._headerLine;
		this._autoDetectDataType = newParams._autoDetectDataType;
		this._delimiter = newParams._delimiter;
		this._quote     = newParams._quote;
		this._allowMultiLineField = newParams._allowMultiLineField;
		this._enableQuoteEscape = newParams._enableQuoteEscape;
	}

	/**
	 * このオブジェクトのパラメータを、指定されたCSVパラメータにコピーする。
	 * @param params	CSVパラメータを受け取るバッファ
	 */
	public void getParameters(CsvParameters params) {
		params._headerLine = this._headerLine;
		params._autoDetectDataType = this._autoDetectDataType;
		params._delimiter = this._delimiter;
		params._quote     = this._quote;
		params._allowMultiLineField = this._allowMultiLineField;
		params._enableQuoteEscape = this._enableQuoteEscape;
	}

	@Override
	public CsvParameters clone() {
		try {
			CsvParameters params = (CsvParameters)super.clone();
			return params;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		int result = 1;
		//--- header line
        result = 31 * result + (_headerLine ? 1231 : 1237);
		//--- auto detect data type
        result = 31 * result + (_autoDetectDataType ? 1231 : 1237);
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
		
		if (obj instanceof CsvParameters) {
			CsvParameters param = (CsvParameters)obj;
			if (param._headerLine==this._headerLine
				&& param._autoDetectDataType==this._autoDetectDataType
				&& param._delimiter==this._delimiter
				&& param._quote==this._quote
				&& param._allowMultiLineField==this._allowMultiLineField
				&& param._enableQuoteEscape==this._enableQuoteEscape)
			{
				return true;
			}
		}
		
		// not equal
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("[headerLine(");
		sb.append(_headerLine);
		sb.append("), autoDetectDataType(");
		sb.append(_autoDetectDataType);
		sb.append("), delimiter(");
		sb.append(_delimiter);
		sb.append("), quote(");
		sb.append(_quote);
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
}
