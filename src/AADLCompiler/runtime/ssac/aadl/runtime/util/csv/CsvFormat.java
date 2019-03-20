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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvFormat.java	2.2.0	2015/06/01
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.csv;

import java.util.Arrays;

import ssac.aadl.runtime.util.Strings;

/**
 * CSVフォーマットの定義を保持するクラス。<br>
 * このクラスは、フィールド区切り文字、クオート文字、クオートエスケープの有無、
 * レコード区切り文字、レコード区切り文字のエスケープの有無の指定を保持する。
 * <p>
 * このクラスインスタンス生成直後では、フィールド区切り文字はカンマ(,)、
 * クオート文字はダブルクオーテーション(&quot;)、クオートによるエスケープは有効、
 * レコード区切り文字は改行文字、改行文字のエスケープは有効、となるように
 * 設定されている。
 * <p>
 * なお、このオブジェクトは同期化されない。
 * 
 * @version 2.2.0
 * @since 2.2.0
 */
public class CsvFormat implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 標準的な CSV フォーマットの定義。
	 */
	static public final CsvFormat	BasicCsvFormat = new CsvFormat();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** プラットフォームに応じた改行文字パターンの配列 **/
	static protected final String[]	_defRecDelimiters;
	static {
		String _newLine = System.getProperty("line.separator");
		if ("\r".equals(_newLine)) {
			// CR
			_defRecDelimiters = new String[]{
					"\r", "\r\n", "\n"
			};
		}
		else if ("\n".equals(_newLine)) {
			// LF
			_defRecDelimiters = new String[]{
					"\n", "\r\n", "\r"
			};
		}
		else {
			// CRLF
			_defRecDelimiters = new String[]{
					"\r\n", "\r", "\n"
			};
		}
	}

	/** レコード区切り文字の配列(先頭が出力時のレコード区切り文字) **/
	private String[]	_recDelimiters;
	/** フィールド区切り文字 **/
	private char		_fldDelimiter;
	/** クオート文字 **/
	private char		_quote;
	/** クオートエスケープの有効無効 **/
	private boolean		_enableQuoteEscape;
	/** レコード区切り文字エスケープの有効無効(<em>_enableQuoteEscape</em> が <tt>false</tt> なら、無視) **/
	private boolean		_enableRecDelimInField;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFormat() {
		this(',', '\"', true);
	}
	
	public CsvFormat(char fieldDelimiter) {
		this(fieldDelimiter, '\"', true);
	}
	
	public CsvFormat(char fieldDelimiter, char quote, boolean enableQuoteEscape) {
		this(fieldDelimiter, quote, enableQuoteEscape, _defRecDelimiters, true);
	}
	
	public CsvFormat(char fieldDelimiter, char quote, boolean enableQuoteEscape, String[] recordDelimiters, boolean enableRecDelimInField) {
		_fldDelimiter          = fieldDelimiter;
		_quote                 = quote;
		_enableQuoteEscape     = enableQuoteEscape;
		_enableRecDelimInField = enableRecDelimInField;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public char getFieldDelimiter() {
		return _fldDelimiter;
	}
	
	public void setFieldDelimiter(char fieldDelimiter) {
		_fldDelimiter = fieldDelimiter;
	}
	
	public boolean updateFieldDelimiter(char fieldDelimiter) {
		if (fieldDelimiter == _fldDelimiter)
			return false;
		_fldDelimiter = fieldDelimiter;
		return true;
	}
	
	public char getQuoteCharacter() {
		return _quote;
	}
	
	public void setQuoteCharacter(char quote) {
		_quote = quote;
	}
	
	public boolean updateQuoteCharacter(char quote) {
		if (quote == _quote)
			return false;
		_quote = quote;
		return true;
	}
	
	public boolean getQuoteEscapeEnabled() {
		return _enableQuoteEscape;
	}
	
	public void setQuoteEscapeEnabled(boolean toEnable) {
		_enableQuoteEscape = toEnable;
	}
	
	public boolean updateQuoteEscapeEnabled(boolean toEnable) {
		if (toEnable == _enableQuoteEscape)
			return false;
		_enableQuoteEscape = toEnable;
		return true;
	}
	
	public String getOutputRecordDelimiter() {
		if (_recDelimiters.length > 0) {
			return _recDelimiters[0];
		} else {
			return null;
		}
	}
	
	public String[] getRecordDelimiters() {
		return _recDelimiters;
	}
	
	public void setRecordDelimiters(String...delimPatterns) {
		if (delimPatterns == null || delimPatterns.length == 0) {
			_recDelimiters = _copyDefaultRecordDelimiters();
		}
		else {
			_recDelimiters = _copyValidRecordDelimiters(delimPatterns);
		}
	}
	
	public boolean updateRecordDelimiters(String...delimPatterns) {
		if (delimPatterns == null || delimPatterns.length == 0) {
			if (!Arrays.equals(_recDelimiters, _defRecDelimiters)) {
				_recDelimiters = _copyDefaultRecordDelimiters();
				return true;
			}
		}
		else if (!Arrays.equals(_recDelimiters, delimPatterns)) {
			_recDelimiters = _copyValidRecordDelimiters(delimPatterns);
			return true;
		}
		// unmodified
		return false;
	}
	
	public boolean getRecordDelimiterInFieldEnabled() {
		return _enableRecDelimInField;
	}
	
	public void setRecordDelimiterInFieldEnabled(boolean toEnable) {
		_enableRecDelimInField = toEnable;
	}
	
	public boolean updateRecordDelimiterInFieldEnabled(boolean toEnable) {
		if (toEnable == _enableRecDelimInField)
			return false;
		_enableRecDelimInField = toEnable;
		return true;
	}
	
	public boolean isAllowedRecordDelimiterInField() {
		return (_enableQuoteEscape && _enableRecDelimInField);
	}

	@Override
	public CsvFormat clone() {
		try {
			CsvFormat inst = (CsvFormat)super.clone();
			String[] recdelims = new String[_recDelimiters.length];
			System.arraycopy(_recDelimiters, 0, recdelims, 0, _recDelimiters.length);
			inst._recDelimiters = recdelims;
			return inst;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError("CsvFormat class cannot be clone.");
		}
	}
	
	@Override
	public int hashCode() {
		int h = (int)_fldDelimiter;
		h = 31 * h + (int)_quote;
		h = 31 * h + (_enableQuoteEscape ? 1231 : 1237);
		h = 31 * h + (_enableRecDelimInField ? 1231 : 1237);
		h = 31 * h + Arrays.hashCode(_recDelimiters);
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof CsvFormat) {
			CsvFormat aFormat = (CsvFormat)obj;
			if (aFormat._fldDelimiter == this._fldDelimiter
				&& aFormat._quote == this._quote
				&& aFormat._enableQuoteEscape == this._enableQuoteEscape
				&& aFormat._enableRecDelimInField == this._enableRecDelimInField
				&& Arrays.equals(aFormat._recDelimiters, this._recDelimiters))
			{
				return true;
			}
		}
		
		// not equals
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("[fieldDelimiter=");
		Strings.appendJsonString(sb, String.valueOf(_fldDelimiter));
		sb.append(", quote=");
		Strings.appendJsonString(sb, String.valueOf(_quote));
		sb.append(", enableQuoteEscape=");
		sb.append(_enableQuoteEscape);
		sb.append(", enableRecDelimInField=");
		sb.append(_enableRecDelimInField);
		sb.append(", recordDelimiters=[");
		for(int i = 0; i < _recDelimiters.length; ++i) {
			if (i > 0) {
				sb.append(',');
			}
			Strings.appendJsonString(sb, _recDelimiters[i]);
		}
		sb.append("]]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected String[] _copyDefaultRecordDelimiters() {
		String[] ret = new String[_defRecDelimiters.length];
		System.arraycopy(_defRecDelimiters, 0, ret, 0, _defRecDelimiters.length);
		return ret;
	}
	
	protected String[] _copyValidRecordDelimiters(String[] delimPatterns) {
		String[] ret = new String[delimPatterns.length];
		for (int i = 0; i < delimPatterns.length; ++i) {
			String pat = delimPatterns[i];
			if (pat == null) {
				throw new NullPointerException("'delimPatterns[" + i + "]' is null.");
			}
			else if (pat.isEmpty()) {
				throw new IllegalArgumentException("'delimPatterns[" + i + "]' is empty.");
			}
			ret[i] = pat;
		}
		return ret;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
