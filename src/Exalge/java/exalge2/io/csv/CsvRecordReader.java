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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)CsvRecordReader.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvRecordReader.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * @deprecated このクラスは互換性のためだけに残されています。新しい {@link exalge2.io.csv.CsvReader.CsvRecord} を使用してください。
 * 
 * CSV ファイルの単一レコード読み込み機能を提供する Reader クラス。
 * <br>
 * このクラスは、{@link CsvReader#readRecord()} により生成される。
 * <p>
 * <strong>(注)</strong> このクラスは、標準 Java {@link java.io.Reader Reader} インタフェースの
 * 拡張ではない。
 * <p>
 * CSV ファイルの定義(空行、コメント行、カラム区切り文字)は、{@link CsvReader} の設定に従う。
 * カラム区切り文字については、{@link CsvReader#setDelimiterChar(char)} で区切り文字が変更されている場合、
 * 設定された区切り文字を使用する。
 * 
 * @version 0.982	2009/09/13
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
 */
public class CsvRecordReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * カラム区切り文字
	 */
	private final char _delimiter;
	
	/**
	 * CSV ファイルの位置情報({@link CsvReader#getCsvLocator()} が返すものと同一のインスタンス)
	 */
	private final CsvLocator		_locator;

	/**
	 * 現在のレコードを表す文字列
	 */
	private String			_recordString;
	/**
	 * 読み込み対象のストリーム
	 */
	private StringReader	_recordReader;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された情報で、新規 <code>CsvRecordReader</code> インスタンスを生成する。
	 */
	protected CsvRecordReader(String oneRecord, CsvLocator locator, final char delimiter) {
		this._delimiter = delimiter;
		this._recordString = oneRecord;
		this._recordReader = new StringReader(oneRecord);
		this._locator = locator;
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------
	
	/**
	 * 現在の {@link CsvLocator} を返す。
	 * <br>
	 * ロケーターに保持される位置情報は、次に読み込まれる位置を示す。
	 * 
	 * @return 現在のロケーター
	 */
	protected CsvLocator getCsvLocator() {
		return this._locator;
	}
	
	/**
	 * 現在のレコードが存在する行番号を返す。
	 * 
	 * @return 行番号
	 */
	public int getLineNumber() {
		return this._locator.getLineNumber();
	}
	
	/**
	 * 次に読み込まれるカラム番号を返す。
	 * 
	 * @return 次に読み込まれるカラム番号
	 */
	public int getColumnNumber() {
		return this._locator.getColumnNumber();
	}
	
	/**
	 * 現在のカラム区切り文字を返す。
	 * 
	 * @return カラム区切り文字
	 */
	public char getDelimiterChar() {
		return this._delimiter;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ストリームを閉じる。
	 * <p>
	 * ここで閉じられるストリームは、単一レコード読み込み専用のストリームであり、
	 * {@link CsvReader} が保持するストリームは閉じられない。
	 */
	public void close() {
		this._recordReader.close();
	}

	/**
	 * 現在のレコードが空行かどうかを検証する。
	 * 
	 * @return 現在のレコードが空行であれば <tt>true</tt>
	 */
	public boolean isBlankLine() {
		if (_recordString != null) {
			// 前後空白を除去した文字列長が 0 以下のものは空白行とする
			return (_recordString.trim().length() <= 0);
		}
		else {
			// 文字列なし
			return true;
		}
	}

	/**
	 * 現在のレコードがコメント行かどうかを検証する。
	 * 
	 * @return 現在のレコードがコメント行であれば <tt>true</tt>
	 */
	public boolean isCommentLine() {
		// 行頭文字が'#'のものは、コメントとみなす
		if (_recordString != null && _recordString.charAt(0) == '#') {
			// コメント行はスキップ
			return true;
		}
		else {
			// コメント行ではないので、スキップしない
			return false;
		}
		
	}

	/**
	 * 新しいカラムを読み込み、{@link java.math.BigDecimal} に変換する。
	 * <br>
	 * 読み込まれたカラム文字列の前後空白は除去され、カラム文字列を
	 * {@link java.math.BigDecimal} インスタンスに変換する。
	 * 変換できない場合はエラーとなる。
	 * <p>
	 * <code>allowOmitted</code> に <tt>true</tt> を指定しており、空白を取り除いた
	 * 文字列が空文字列となる場合、エラーとならない。この場合、<tt>null</tt> が返される。
	 * 
	 * @param allowOmitted 空文字列を許可する場合は <tt>true</tt>
	 * @return 変換した数値を保持する {@link java.math.BigDecimal} インスタンス
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException 数値への変換に失敗した場合
	 */
	public BigDecimal readBigDecimalColumn(boolean allowOmitted)
		throws IOException, CsvFormatException
	{
		this._locator.incrementColumnNumber();
		String strColumn = readToTokenWithQuote(_recordReader, getDelimiterChar());
		//--- 前後の空白は除去
		strColumn = strColumn.trim();
		
		// check column
		if (allowOmitted && strColumn.length() <= 0) {
			return null;
		}
		
		// convert to BigDecimal
		BigDecimal retValue = null;
		try {
			retValue = new BigDecimal(strColumn);
		}
		catch (NumberFormatException ex) {
			throw new CsvFormatException(CsvFormatException.NUMBER_FORMAT_EXCEPTION, this._locator.getLineNumber(), this._locator.getColumnNumber()-1);
		}
		
		return retValue;
	}

	/**
	 * 新しいカラムを読み込む。
	 * <br>
	 * このメソッドは、読み込まれたカラム文字列そのものを返す。
	 * <p>
	 * 読み込まれたカラム文字列が空文字列の場合の挙動は、<code>allowOmitted</code> により異なる。
	 * <ul>
	 * <li><tt>true</tt> を指定した場合は、<tt>null</tt> を返す。
	 * <li><tt>false</tt> を指定した場合は、エラー要因が {@link CsvFormatException#COLUMN_OMITTED} の
	 *  {@link CsvFormatException} 例外をスローする。
	 * </ul>
	 * 
	 * @param allowOmitted 空文字列を許可する場合は <tt>true</tt>
	 * 
	 * @return 読み込まれたカラム文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException 読み込まれたカラム文字列が空文字列で <code>allowOmitted</code> に
	 *  <tt>true</tt> が指定されている場合
	 */
	public String readStringColumn(boolean allowOmitted)
		throws IOException, CsvFormatException
	{
		this._locator.incrementColumnNumber();
		String strColumn = readToTokenWithQuote(_recordReader, getDelimiterChar());
		
		// check column
		if (strColumn.length() <= 0) {
			if (allowOmitted) {
				return null;
			}
			else {
				// 省略不可
				throw new CsvFormatException(CsvFormatException.COLUMN_OMITTED, this._locator.getLineNumber(), this._locator.getColumnNumber()-1);
			}
		}
		
		return strColumn;
	}
	
	/**
	 * 新しいカラムを読み込み、指定されたパターンと一致するかを検証する。
	 * <br>
	 * このメソッドは、読み込まれたカラム文字列そのものと指定されたパターンとの一致を検証し、
	 * 一致した場合に読み込まれたカラム文字列を返す。
	 * パターンに一致しなかった場合は、エラー要因が {@link CsvFormatException#ILLEGAL_COLUMN_PATTERN} の
	 *  {@link CsvFormatException} 例外をスローする。
	 * <p>
	 * 読み込まれたカラム文字列が空文字列の場合の挙動は、<code>allowOmitted</code> により異なる。
	 * <ul>
	 * <li><tt>true</tt> を指定した場合は、<tt>null</tt> を返す。
	 * <li><tt>false</tt> を指定した場合は、エラー要因が {@link CsvFormatException#COLUMN_OMITTED} の
	 *  {@link CsvFormatException} 例外をスローする。
	 * </ul>
	 * 
	 * @param matchingPattern 一致を検証するためのパターン
	 * @param allowOmitted 空文字列を許可する場合は <tt>true</tt>
	 * 
	 * @return 読み込まれたカラム文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラム文字列が条件に一致しない場合
	 */
	public String readStringColumn(Pattern matchingPattern, boolean allowOmitted)
		throws IOException, CsvFormatException
	{
		this._locator.incrementColumnNumber();
		String strColumn = readToTokenWithQuote(_recordReader, getDelimiterChar());
		
		// check column
		if (strColumn.length() <= 0) {
			if (allowOmitted) {
				return null;
			}
			else {
				// 省略不可
				throw new CsvFormatException(CsvFormatException.COLUMN_OMITTED, this._locator.getLineNumber(), this._locator.getColumnNumber()-1);
			}
		}
		
		// check by matching pattern
		if (!matchingPattern.matcher(strColumn).matches()) {
			throw new CsvFormatException(strColumn, CsvFormatException.ILLEGAL_COLUMN_PATTERN, this._locator.getLineNumber(), this._locator.getColumnNumber()-1);
		}
		
		return strColumn;
	}
	
	/**
	 * 新しいカラムを読み込む。
	 * <br>
	 * このメソッドは、読み込まれたカラム文字列から前後の空白を除去した文字列を返す。
	 * <p>
	 * 前後空白が除去された後のカラム文字列が空文字列の場合の挙動は、<code>allowOmitted</code> により異なる。
	 * <ul>
	 * <li><tt>true</tt> を指定した場合は、<tt>null</tt> を返す。
	 * <li><tt>false</tt> を指定した場合は、エラー要因が {@link CsvFormatException#COLUMN_OMITTED} の
	 *  {@link CsvFormatException} 例外をスローする。
	 * </ul>
	 * 
	 * @param allowOmitted 空文字列を許可する場合は <tt>true</tt>
	 * 
	 * @return 読み込まれたカラム文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException 読み込まれたカラム文字列が空文字列で <code>allowOmitted</code> に
	 *  <tt>true</tt> が指定されている場合
	 */
	public String readTrimmedStringColumn(boolean allowOmitted)
		throws IOException, CsvFormatException
	{
		this._locator.incrementColumnNumber();
		String strColumn = readToTokenWithQuote(_recordReader, getDelimiterChar());
		
		// Trim
		strColumn = strColumn.trim();
		
		// check column
		if (strColumn.length() <= 0) {
			if (allowOmitted) {
				return null;
			}
			else {
				// 省略不可
				throw new CsvFormatException(CsvFormatException.COLUMN_OMITTED, this._locator.getLineNumber(), this._locator.getColumnNumber()-1);
			}
		}
		
		return strColumn;
	}
	
	/**
	 * 新しいカラムを読み込み、指定されたパターンと一致するかを検証する。
	 * <br>
	 * このメソッドは、読み込まれたカラム文字列から前後の空白が除去されたものと指定されたパターンとの一致を検証し、
	 * 一致した場合に読み込まれたカラム文字列を返す。
	 * パターンに一致しなかった場合は、エラー要因が {@link CsvFormatException#ILLEGAL_COLUMN_PATTERN} の
	 *  {@link CsvFormatException} 例外をスローする。
	 * <p>
	 * 前後空白が除去された後のカラム文字列が空文字列の場合の挙動は、<code>allowOmitted</code> により異なる。
	 * <ul>
	 * <li><tt>true</tt> を指定した場合は、<tt>null</tt> を返す。
	 * <li><tt>false</tt> を指定した場合は、エラー要因が {@link CsvFormatException#COLUMN_OMITTED} の
	 *  {@link CsvFormatException} 例外をスローする。
	 * </ul>
	 * 
	 * @param matchingPattern 一致を検証するためのパターン
	 * @param allowOmitted 空文字列を許可する場合は <tt>true</tt>
	 * 
	 * @return 読み込まれたカラム文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラム文字列が条件に一致しない場合
	 */
	public String readTrimmedStringColumn(Pattern matchingPattern, boolean allowOmitted)
		throws IOException, CsvFormatException
	{
		this._locator.incrementColumnNumber();
		String strColumn = readToTokenWithQuote(_recordReader, getDelimiterChar());
		
		// Trim
		strColumn = strColumn.trim();
		
		// check column
		if (strColumn.length() <= 0) {
			if (allowOmitted) {
				return null;
			}
			else {
				// 省略不可
				throw new CsvFormatException(CsvFormatException.COLUMN_OMITTED, this._locator.getLineNumber(), this._locator.getColumnNumber()-1);
			}
		}
		
		// check by matching pattern
		if (!matchingPattern.matcher(strColumn).matches()) {
			throw new CsvFormatException(strColumn, CsvFormatException.ILLEGAL_COLUMN_PATTERN, this._locator.getLineNumber(), this._locator.getColumnNumber()-1);
		}
		
		return strColumn;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ストリームの現在位置からカラム文字列を取り出す。
	 * ダブルクオートで開始される場合は、次のダブルクオートもしくは行末までを同一カラムとして読み込む。
	 * 
	 * @param argReader Reader
	 * @param delimiter カラム区切り文字
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	protected String readToTokenWithQuote(final Reader argReader, final char delimiter)
		throws IOException
	{
	    final StringBuffer sb = new StringBuffer();
	    
	    boolean isStarted = false;
	    boolean isEnded = false;
	    
	    for (;;) {
	    	int iRead = argReader.read();
	    	// check terminate
	    	if (iRead < 0) {
	    		// 読み込み終了
	    		break;
	    	}
	    	// check delimiter
	    	if (iRead == delimiter) {
	    		// 区切り文字
	    		if (isStarted && !isEnded) {
	    			// ダブルクオート内
	    			sb.append((char)iRead);
	    		}
	    		else {
	    			// 区切り文字なので終了
	    			break;
	    		}
	    	}
	    	else if (iRead == '"') {
	    		// ダブルクオート
	    		if (!isStarted) {
	    			if (sb.length() <= 0) {
	    				// 開始ダブルクオート
	    				isStarted = true;
	    			} else {
	    				// 文字として扱う
	    				sb.append((char)iRead);
	    			}
	    		}
	    		else if (!isEnded) {
	    			// 終了ダブルクオートとしてマーク
	    			isEnded = true;
	    		} else {
	    			// ダブルクオートペアとして処理
					sb.append((char)iRead);
					//--- 終了ダブルクオートマークはリセット
					isEnded = false;
	    		}
	    	}
	    	else {
	    		sb.append((char)iRead);
	    	}
	    }
	    return sb.toString();
	}
}
