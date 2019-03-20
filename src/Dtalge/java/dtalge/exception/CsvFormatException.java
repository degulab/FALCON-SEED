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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)CsvFormatException.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.exception;

/**
 * データ代数パッケージの CSV ファイル入力で読み込みエラーが発生した
 * 場合にスローされる。
 * <p>
 * この例外は、エラー発生の要因、ならびに、エラー発生箇所の目安となる
 * ファイル内位置(行番号、CSV フィールド番号)を保持する。
 * <br>
 * ファイル内位置の行番号、ならびにフィールド番号は、1 から始まる数値で示される。
 * 
 * @version 0.10	2008/08/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.10
 *
 */
public class CsvFormatException extends Exception
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final int NO_LINE_NUMBER = -1;
	private static final int NO_COLUMN_NUMBER = -1;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * エラー発生箇所のレコード先頭行番号(1～)
	 */
	private int lineNo;
	/**
	 * エラー発生箇所の CSV フィールド番号(1～)
	 */
	private int fieldNo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 詳細情報を持たない新規例外を構築する。
	 */
	public CsvFormatException() {
		super();
		initialize(NO_LINE_NUMBER, NO_COLUMN_NUMBER);
	}

	/**
	 * 指定された詳細メッセージのみを持つ新規例外を構築する。
	 * 
	 * @param message 詳細メッセージ
	 */
	public CsvFormatException(String message) {
		super(message);
		initialize(NO_LINE_NUMBER, NO_COLUMN_NUMBER);
	}

	/**
	 * 指定されたエラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param lineNo	エラー発生箇所を示す行番号
	 * @param fieldNo	エラー発生箇所を示すフィールド番号
	 */
	public CsvFormatException(int lineNo, int fieldNo) {
		super();
		initialize(lineNo, fieldNo);
	}

	/**
	 * 指定された詳細メッセージ、エラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param message	詳細メッセージ
	 * @param lineNo	エラー発生箇所を示す行番号
	 * @param fieldNo	エラー発生箇所を示すフィールド番号
	 */
	public CsvFormatException(String message, int lineNo, int fieldNo) {
		super(message);
		initialize(lineNo, fieldNo);
	}
	
	private void initialize(int lineNo, int fieldNo) {
		this.lineNo = lineNo;
		this.fieldNo = fieldNo;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

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
