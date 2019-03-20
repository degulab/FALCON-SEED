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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)NaturalNumberRangeTokenizer.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

/**
 * 1 以上の自然数によって指定される不等間隔の数値範囲定義文字列を解析するクラス。<br>
 * このクラスで解析する数値範囲は、連続する数値範囲をハイフンで、連続ではない
 * 数値範囲をカンマで区切って記述する。例えば、1、3、5～10、12～15、という数値範囲の
 * 場合、&quot;1,3,5-10,12-15&quot; というように記述する。
 * <p>このクラスでは、数値範囲書式に従い入力文字列を解析し、分割されたトークンを
 * 順次返すもので、数字、ハイフン、カンマ以外の文字は不正な文字トークンとしてその情報を
 * 返す。そのため、不正な文字でも例外などはスローしない。
 * 
 * @version 1.70	2011/06/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 * 
 * @since 1.70
 */
public class NaturalNumberRangeTokenizer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** トークン種別：入力文字列の終端に到達していることを示す **/
	static public final int EOT		= (-1);
	/** トークン種別：区切り文字であることを示す **/
	static public final int DELIMITER	= (-2);
	/** トークン種別：連続記号であることを示す **/
	static public final int SERIAL	= (-3);
	/** トークン種別：数字のみの文字列であることを示す **/
	static public final int NUMBER	= (-4);
	/** トークン種別：数字以外を含むトークンであることを示す **/
	static public final int INVALID	= (-9);

	/** 範囲の区切り文字 **/
	static public final char		DELIMITER_CHAR		= ',';
	/** 範囲の区切り文字 **/
	static public final String	DELIMITER_STRING	= "" + DELIMITER_CHAR;
	/** 連続範囲を示す文字 **/
	static public final char 	SERIAL_CHAR		= '-';
	/** 連続範囲を示す文字 **/
	static public final String	SERIAL_STRING	= "" + SERIAL_CHAR;
	/** 無効なトークン位置を示す **/
	static public final int	INVALID_POSITION	= (-1);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 入力文字列 **/
	private final String	_input;

	/** 次のトークン位置 **/
	private int	_nextPosition;
	/** 最後のトークン位置 **/
	private int	_lastPosition;
	/** 最後のトークン種別 **/
	private int	_lastType;
	/** 最後のトークン **/
	private String	_lastToken;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された入力文字列でインスタンスを初期化する。
	 * @param strInput	入力文字列
	 */
	public NaturalNumberRangeTokenizer(final String strInput) {
		this._input = strInput;
		
		if (strInput != null && strInput.length() > 0) {
			setLastToken(INVALID, 0, INVALID_POSITION, "");
		} else {
			setEndOfToken();
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 最後に取得したトークンの種別を返す。
	 * @return	最後に取得したトークン種別
	 */
	public int getLastTokenType() {
		return _lastType;
	}

	/**
	 * 最後に取得したトークンを返す。
	 * @return	最後に取得したトークン
	 */
	public String getLastToken() {
		return _lastToken;
	}

	/**
	 * 最後に取得したトークンの位置を返す。
	 * @return	最後に取得したトークンの開始位置
	 */
	public int getLastTokenPosition() {
		return _lastPosition;
	}

	/**
	 * 次のトークンを取得する。
	 * @return	取得したトークンの種別
	 */
	public int nextToken() {
		if (EOT != _lastType) {
			return parseNextToken(_input, _nextPosition);
		} else {
			// already EOT reached
			return EOT;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 最後のトークンを設定する。
	 * @param type		
	 * @param nextPos	
	 * @param tokenPos	
	 * @param token		
	 * @return	最後のトークン種別
	 */
	protected int setLastToken(final int type, final int nextPos, final int tokenPos, final String token) {
		this._lastType = type;
		this._lastPosition = tokenPos;
		this._lastToken = token;
		this._nextPosition = nextPos;
		return this._lastType;
	}

	/**
	 * トークンが終了したことを設定する。
	 * @return	トークンが終了したことを示すトークン種別
	 */
	protected int setEndOfToken() {
		this._lastType = EOT;
		this._lastPosition = INVALID_POSITION;
		this._lastToken = null;
		this._nextPosition = INVALID_POSITION;
		return EOT;
	}

	/**
	 * 次のトークンを取得する。
	 * @param strInput	入力文字列
	 * @param offset	解析開始位置
	 * @return	取得したトークンの種別
	 */
	protected int parseNextToken(final String strInput, int offset) {
		int  len = strInput.length();
		
		// check EOT
		if (offset >= len) {
			return setEndOfToken();
		}

		// get first character
		char ch = strInput.charAt(offset);
		
		// skip before whitespaces
		if (Character.isWhitespace(ch)) {
			for (++offset; offset < len; offset++) {
				ch = strInput.charAt(offset);
				if (!Character.isWhitespace(ch)) {
					break;
				}
			}
			//--- check EOT
			if (offset >= len) {
				return setEndOfToken();
			}
		}
		
		// check delimiter
		if (DELIMITER_CHAR == ch) {
			//--- 空白と Delimiter の連続は、一つの Delimiter にする
			int spos = offset;
			for (++offset; offset < len; offset++) {
				ch = strInput.charAt(offset);
				if (DELIMITER_CHAR != ch && !Character.isWhitespace(ch)) {
					break;
				}
			}
			return setLastToken(DELIMITER, offset, spos, DELIMITER_STRING);
		}
		
		// check serial
		if (SERIAL_CHAR == ch) {
			//--- 空白と Serial の連続は、一つの Serial にする
			int spos = offset;
			for (++offset; offset < len; offset++) {
				ch = strInput.charAt(offset);
				if (SERIAL_CHAR != ch && !Character.isWhitespace(ch)) {
					break;
				}
			}
			return setLastToken(SERIAL, offset, spos, SERIAL_STRING);
		}
		
		// check number
		if (!Character.isDigit(ch)) {
			// invalid token
			int spos = offset;
			for (++offset ; offset < len; offset++) {
				ch = strInput.charAt(offset);
				//--- INVALID の場合は、区切り文字のみを探す
				if (DELIMITER_CHAR==ch) {
					break;
				}
			}
			return setLastToken(INVALID, offset, spos, strInput.substring(spos, offset));
		}
		
		// parse number
		int spos = offset;
		++offset;
		int epos = offset;
		for ( ; offset < len; ) {
			ch = strInput.charAt(offset);
			
			if (DELIMITER_CHAR==ch || SERIAL_CHAR==ch) {
				break;
			}
			else if (Character.isWhitespace(ch)) {
				// skip after whitespaces
				for (++offset; offset < len; offset++) {
					ch = strInput.charAt(offset);
					if (DELIMITER_CHAR==ch || SERIAL_CHAR==ch) {
						break;
					}
					else if (!Character.isWhitespace(ch)) {
						//--- illegal whitespace position
						for (++offset; offset < len; offset++) {
							ch = strInput.charAt(offset);
							//--- INVALID の場合は、区切り文字のみを探す
							if (DELIMITER_CHAR==ch) {
								break;
							}
						}
						return setLastToken(INVALID, offset, spos, strInput.substring(spos, offset));
					}
				}
				break;	// after whiltespaces skipped
			}
			else if (!Character.isDigit(ch)) {
				// invalid token
				for (++offset; offset < len; offset++) {
					ch = strInput.charAt(offset);
					//--- INVALID の場合は、区切り文字のみを探す
					if (DELIMITER_CHAR==ch) {
						break;
					}
				}
				return setLastToken(INVALID, offset, spos, strInput.substring(spos, offset));
			}
			//--- next position
			++offset;
			epos = offset;
		}
		
		// set parsed number token
		return setLastToken(NUMBER, offset, spos, strInput.substring(spos, epos));
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
