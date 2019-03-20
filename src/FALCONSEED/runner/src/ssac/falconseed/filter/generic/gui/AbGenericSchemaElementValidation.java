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
 * @(#)GenericSchemaElementValidation.java	3.2.1	2015/07/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import ssac.aadl.runtime.util.Objects;

/**
 * 汎用フィルタのスキーマ定義データモデルの要素における値正当性チェック機能のインタフェースの共通実装。
 * 
 * @version 3.2.1
 * @since 3.2.1
 */
public abstract class AbGenericSchemaElementValidation implements GenericSchemaElementValidation
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** エラーメッセージ **/
	private String		_strErrorMessage;
	/** エラー要因 **/
	private Throwable	_exErrorCause;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbGenericSchemaElementValidation() {
		_strErrorMessage = null;
		_exErrorCause    = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implements GenericSchemaElementValidation interfaces
	//------------------------------------------------------------

	/**
	 * エラーの有無を返す。
	 * @return	エラーがある場合は <tt>true</tt>、エラーのない場合は <tt>false</tt>
	 */
	@Override
	public boolean hasError() {
		return (_strErrorMessage != null || _exErrorCause != null);
	}

	/**
	 * 有効なエラーメッセージを返す。
	 * @return	有効なエラーメッセージ、エラーが存在しない場合は <tt>null</tt>
	 */
	@Override
	public String getAvailableErrorMessage() {
		if (_strErrorMessage != null) {
			if (_exErrorCause != null) {
				// message & cause
				return (_strErrorMessage + " : " + _exErrorCause.toString());
			} else {
				// only message
				return _strErrorMessage;
			}
		}
		else if (_exErrorCause != null) {
			// only cause
			return _exErrorCause.toString();
		}
		else {
			// no error
			return null;
		}
	}

	/**
	 * 設定されているエラーメッセージを返す。
	 * @return	設定されているエラーメッセージ、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getErrorMessage() {
		return _strErrorMessage;
	}

	/**
	 * 設定されているエラー要因を返す。
	 * @return	エラー要因となる例外オブジェクト、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public Throwable getErrorCause() {
		return _exErrorCause;
	}

	/**
	 * エラー情報をクリアする。
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean clearError() {
		if (!hasError())
			return false;
		//--- modified
		_strErrorMessage = null;
		_exErrorCause    = null;
		return true;
	}

	/**
	 * エラーメッセージを設定する。
	 * @param errmsg	設定するエラーメッセージ、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(String errmsg) {
		if (Objects.equals(errmsg, _strErrorMessage))
			return false;
		//--- modified
		_strErrorMessage = errmsg;
		return true;
	}

	/**
	 * エラー要因を設定する。
	 * @param cause	設定するエラー要因、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(Throwable cause) {
		if (Objects.equals(cause, _exErrorCause))
			return false;
		//--- modified
		_exErrorCause = cause;
		return true;
	}

	/**
	 * エラーメッセージとエラー要因を設定する。
	 * @param errmsg	設定するエラーメッセージ、もしくは <tt>null</tt>
	 * @param cause		設定するエラー要因、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(String errmsg, Throwable cause) {
		if (Objects.equals(errmsg, _strErrorMessage) && Objects.equals(cause, _exErrorCause))
			return false;
		//--- modified
		_strErrorMessage = errmsg;
		_exErrorCause    = cause;
		return true;
	}

	//------------------------------------------------------------
	// Implements Object interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの文字列表現を返す。
	 * このオブジェクトが返す文字列は、エラーが設定されている場合は {@link #getAvailableErrorMessage()} が返す文字列、
	 * エラーが設定されていない場合は空文字列となる。
	 * @return	エラー情報を示す文字列、もしくは空文字列
	 * @see #getAvailableErrorMessage()
	 */
	@Override
	public String toString() {
		if (hasError()) {
			return getAvailableErrorMessage();
		} else {
			return "";
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
