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
 * @(#)OutputString.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)OutputString.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import ssac.util.Objects;

/**
 * 文字列と、標準出力またはエラー出力を示すステータスを保持するクラス。
 * このクラスのオブジェクトは不変である。
 * <p>
 * このクラスのインスタンスは、<code>{@link CommandOutput}</code> クラスの
 * 要素として利用される。
 * 
 * @version 1.17	2010/11/19
 */
public class OutputString
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** この出力文字列がエラー出力のものであることを示すフラグ **/
	private final boolean		_isError;
	/** 出力文字列 **/
	private final String		_strOutput;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準出力の内容とする文字列として、新しいインスタンスを生成する。
	 * @param strout	設定する文字列
	 */
	public OutputString(String strout) {
		this(false, strout);
	}

	/**
	 * 標準出力もしくはエラー出力どちらかの文字列として、新しいインスタンスを生成する。
	 * @param isError	エラー出力の文字列とする場合は <tt>true</tt>、標準出力の文字列とする場合は <tt>false</tt> を指定する。
	 * @param strout	設定する文字列
	 */
	public OutputString(boolean isError, String strout) {
		this._isError = isError;
		this._strOutput = strout;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * エラー出力の文字列であるかを検証する。
	 * @return	エラー出力の文字列である場合は <tt>true</tt>、標準出力の文字列である場合は <tt>false</tt> を返す。
	 */
	public boolean isError() {
		return _isError;
	}

	/**
	 * 設定されている文字列を取得する。
	 * @return	文字列
	 */
	public String getString() {
		return _strOutput;
	}

	/**
	 * このオブジェクトのハッシュコードを返す。
	 * @since 1.17
	 */
	@Override
	public int hashCode() {
		if (_isError) {
			return (31 * 1231 + (_strOutput==null ? 0 : _strOutput.hashCode()));
		} else {
			return (31 * 1237 + (_strOutput==null ? 0 : _strOutput.hashCode()));
		}
	}

	/**
	 * このオブジェクトと指定されたオブジェクトが同値かを判定する。
	 * @param obj	判定するオブジェクト
	 * @return	同値であれば <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @since 1.17
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof OutputString) {
			OutputString outstr = (OutputString)obj;
			if (outstr._isError==this._isError && Objects.isEqual(outstr._strOutput, this._strOutput)) {
				return true;
			}
		}
		
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
