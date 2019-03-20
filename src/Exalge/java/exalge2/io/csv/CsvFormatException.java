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
 * @(#)CsvFormatException.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFormatException.java	0.95	2008/10/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFormatException.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.csv;

import exalge2.io.ExalgeIOException;

/**
 * 交換代数コア・パッケージの CSV ファイル入力で読み込みエラーが発生した
 * 場合にスローされる。
 * <p>
 * この例外は、エラー発生箇所の目安となるファイル内位置を示す行番号、ならびに
 * フィールド(カラム)番号を保持する。
 * <br>
 * ファイル内位置の行番号、ならびにフィールド番号は、1 から始まる数値で示される。
 * 
 * @version 0.982	2009/09/13
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
 *
 */
public class CsvFormatException extends ExalgeIOException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * エラー要因が特定されないエラーであることを示す。
	 */
	public static final int NO_FACTOR = -1;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * ダブルクオーテーションで始まるカラムが、ダブルクオーテーションで閉じていないことを示す。
	 */
	public static final int ILLEGAL_QUOTE_ENCODING = 1;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 必須のカラムにデータが存在しないことを示す。
	 */
	public static final int COLUMN_OMITTED = 2;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * カラムのデータが数値に変換できないことを示す。
	 */
	public static final int NUMBER_FORMAT_EXCEPTION = 3;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * カラムのデータに不正な文字が使用されている、もしくはカラムのデータとして正しくないことを示す。
	 */
	public static final int ILLEGAL_COLUMN_PATTERN = 4;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 変換テーブルにおいて、"from" を示す行が存在しないことを示す。
	 */
	public static final int DIR_FROM_NOT_EXIST = 5;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 変換テーブルにおいて、"to" を示す行が存在しないことを示す。
	 */
	public static final int DIR_TO_NOT_EXIST = 6;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 変換テーブルにおいて、同一の変換元基底パターンがすでに存在していることを示す。
	 */
	public static final int DIR_FROM_ALREADY_EXIST = 7;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 按分変換テーブルの変換先按分比率テーブルにおいて、同一の変換先基底パターンが
	 * すでに存在していることを示す。
	 */
	public static final int DIR_TO_ALREADY_EXIST = 8;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 変換先基底パターンにおいて、基底キーにワイルドカードとワイルドカード以外の文字が
	 * 組み合わされていることを示す。
	 */
	public static final int ILLEGAL_DIR_TO_WILDCARD_PATTERN = 9;
	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 変換テーブルにおいて、按分比率が 0 未満であることを示す。
	 */
	public static final int ILLEGAL_DIVIDE_RATIO = 10;
	
	private static final int NO_LINE_NUMBER = -1;
	private static final int NO_COLUMN_NUMBER = -1;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * エラー要因コード
	 */
	private int factor = NO_FACTOR;

	/**
	 * エラー発生箇所のレコード先頭行番号(1～)
	 */
	private final int lineNo;
	/**
	 * エラー発生箇所の CSV フィールド番号(1～)
	 */
	private final int fieldNo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 詳細情報を持たない新規例外を構築する。
	 */
	public CsvFormatException() {
		this(NO_LINE_NUMBER, NO_COLUMN_NUMBER);
	}

	/**
	 * 指定された詳細メッセージのみを持つ新規例外を構築する。
	 * 
	 * @param message 詳細メッセージ
	 */
	public CsvFormatException(String message) {
		this(message, NO_LINE_NUMBER, NO_COLUMN_NUMBER);
	}

	/**
	 * 指定されたエラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param lineNo	エラー発生箇所を示す行番号
	 * @param fieldNo	エラー発生箇所を示すフィールド番号
	 * 
	 * @since 0.982
	 */
	public CsvFormatException(int lineNo, int fieldNo) {
		super();
		this.lineNo  = lineNo;
		this.fieldNo = fieldNo;
	}

	/**
	 * 指定された詳細メッセージ、エラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param message	詳細メッセージ
	 * @param lineNo	エラー発生箇所を示す行番号
	 * @param fieldNo	エラー発生箇所を示すフィールド番号
	 * 
	 * @since 0.982
	 */
	public CsvFormatException(String message, int lineNo, int fieldNo) {
		super(message);
		this.lineNo  = lineNo;
		this.fieldNo = fieldNo;
	}

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 指定されたエラー要因のみを持つ新規例外を構築する。
	 * 
	 * @param factor エラー要因
	 */
	public CsvFormatException(int factor) {
		this(factor, NO_LINE_NUMBER, NO_COLUMN_NUMBER);
	}

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 指定されたエラー要因とエラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param factor エラー要因
	 * @param locator エラー発生箇所を保持する {@link CsvLocator} インスタンス
	 */
	public CsvFormatException(int factor, CsvLocator locator) {
		this(factor, locator.getLineNumber(), locator.getColumnNumber());
	}

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 指定されたエラー要因とエラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param factor		エラー要因
	 * @param lineNumber	エラー発生箇所を示す行番号
	 * @param columnNumber	エラー発生箇所を示すカラム番号
	 */
	public CsvFormatException(int factor, int lineNumber, int columnNumber) {
		this(lineNumber, columnNumber);
		this.factor = factor;
	}

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 指定された詳細メッセージとエラー要因を持つ新規例外を構築する。
	 * 
	 * @param message 詳細メッセージ
	 * @param factor エラー要因
	 */
	public CsvFormatException(String message, int factor) {
		this(message, factor, NO_LINE_NUMBER, NO_COLUMN_NUMBER);
	}

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 指定された詳細メッセージ、エラー要因、エラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param message	詳細メッセージ
	 * @param factor	エラー要因
	 * @param locator	エラー発生箇所を保持する {@link CsvLocator} インスタンス
	 */
	public CsvFormatException(String message, int factor, CsvLocator locator) {
		this(message, factor, locator.getLineNumber(), locator.getColumnNumber());
	}

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 指定された詳細メッセージ、エラー要因、エラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param message		詳細メッセージ
	 * @param factor		エラー要因
	 * @param lineNumber	エラー発生箇所を示す行番号
	 * @param columnNumber	エラー発生箇所を示すカラム番号
	 */
	public CsvFormatException(String message, int factor, int lineNumber, int columnNumber) {
		this(message, lineNumber, columnNumber);
		this.factor = factor;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * @deprecated 要因コードは今後削除されます。例外メッセージを使用してください。
	 * 設定されているエラー要因を返す。
	 * 
	 * @return エラー要因
	 */
	public int getIllegalFactor() {
		return this.factor;
	}

	/**
	 * @deprecated このメソッドは {@link #getFieldNumber()} に置き換わりました。
	 * 設定されているエラー発生箇所のカラム番号を返す。
	 * カラム番号が設定されていない場合は負の値を返す。
	 * 
	 * @return カラム番号
	 */
	public int getColumnNumber() {
		return getFieldNumber();
	}

	/**
	 * 設定されているエラー発生箇所の行番号を返す。
	 * 行番号が設定されていない場合は負の値を返す。
	 * 
	 * @return 行番号
	 */
	public int getLineNumber() {
		return lineNo;
	}

	/**
	 * 設定されているエラー発生箇所のフィールド番号を返す。
	 * フィールド番号が設定されていない場合は負の値を返す。
	 * 
	 * @return フィールド番号
	 * 
	 * @since 0.982
	 */
	public int getFieldNumber() {
		return fieldNo;
	}

	/**
	 * この例外に設定されているエラー発生箇所の行番号、フィールド番号を
	 * 持つ整形された文字列を返す。
	 * この文字列は、次の文字列を連結したものになる。
	 * <ul>
	 * <li>"[Line#: "
	 * <li>行番号(先頭行を 1 とする番号、無効な番号なら 0)
	 * <li>"; Column#: "
	 * <li>フィールド番号(先頭フィールドを 1 とする番号、無効な番号なら 0)
	 * <li>"]"
	 * </ul>
	 * 
	 * @return	整形された文字列
	 * 
	 * @since 0.982
	 */
	public String getPositionString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append("Line#: ");
		sb.append(lineNo > 0 ? lineNo : 0);
		sb.append("; Column#: ");
		sb.append(fieldNo > 0 ? fieldNo : 0);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * このスロー可能オブジェクトの短い記述を返す。
	 * このオブジェクトが非<tt>null<tt>の詳細メッセージ文字列を
	 * 使用して作成された場合、結果は次の文字列を連結したものになる。
	 * <ul>
	 * <li>このオブジェクトの実際のクラス名
	 * <li>"[Line#: "
	 * <li>行番号(先頭行を 1 とする番号、無効な番号なら 0)
	 * <li>"; Column#: "
	 * <li>フィールド番号(先頭フィールドを 1 とする番号、無効な番号なら 0)
	 * <li>"]"
	 * <li>": " (コロンとスペース)
	 * <li>このオブジェクトに対する {@link #getMessage} メソッドの結果
	 * </ul>
	 * このオブジェクトが <tt>null</tt> の詳細メッセージ文字列を使用して
	 * 作成された場合は、(コロンとスペース)以降の文字列は含まれません。
	 * 
	 * @return このスロー可能オブジェクトの文字列表現
	 * 
	 * @since 0.982
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//--- exception class
		sb.append(getClass().getName());
		sb.append(getPositionString());
		//--- message
        String message = getLocalizedMessage();
		if (message != null) {
			sb.append(": ");
			sb.append(message);
		}
		
		return sb.toString();
	}
}
