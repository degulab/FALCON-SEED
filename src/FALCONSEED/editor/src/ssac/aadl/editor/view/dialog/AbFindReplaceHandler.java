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
 * @(#)AbFindReplaceHandler.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbFindReplaceHandler.java	1.10	2008/12/03
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;


/**
 * 検索／置換ダイアログ用ハンドラの基本機能。
 * 
 * @version 1.16	2010/09/27
 * 
 * @since 1.10
 */
public abstract class AbFindReplaceHandler implements FindReplaceInterface
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 検索キーワード
	 */
	static protected String	_keywordString = null;
	/**
	 * 置換キーワード
	 */
	static protected String	_replaceString = null;
	/**
	 * 大文字小文字を区別しないフラグ
	 */
	static protected boolean	_flgIgnoreCase = true;
	/**
	 * 置換した箇所の総数
	 */
	protected int _replaceCount = 0;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 置換した箇所の総数を返す。
	 * @return 置換数
	 */
	public int getLastReplacedCount() {
		return _replaceCount;
	}

	/**
	 * 大文字小文字を区別しない状態であるかを判定する。
	 * @return 大文字小文字を区別しないなら <tt>true</tt>
	 */
	public boolean isIgnoreCase() {
		return _flgIgnoreCase;
	}

	/**
	 * 大文字小文字の区別フラグを設定する。
	 * @param ignore	大文字小文字を区別させない場合は <tt>true</tt> を指定する。
	 */
	public void setIgnoreCase(boolean ignore) {
		_flgIgnoreCase = ignore;
	}

	/**
	 * 検索キーワードを設定する。
	 * @param strKeyword	検索キーワード
	 */
	public void putKeywordString(String strKeyword) {
		_keywordString = strKeyword;
	}

	/**
	 * 置換キーワードを設定する。
	 * @param strKeyword	置換キーワード
	 */
	public void putReplaceString(String strKeyword) {
		_replaceString = strKeyword;
	}

	/**
	 * 設定されている検索キーワードを取得する。
	 * @return	現在の検索キーワードを返す。設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getKeywordString() {
		return _keywordString;
	}

	/**
	 * 設定されている置換キーワードを取得する。
	 * @return 現在の置換キーワードを返す。設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getReplaceString() {
		return _replaceString;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
