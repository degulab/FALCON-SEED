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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EtcConfigCsvData.java	3.3.0	2016/05/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv;

import java.io.File;

import ssac.util.Strings;
import ssac.util.excel2csv.command.CmdCsvFileNode;

/**
 * <code>[Excel to CSV]</code> における変換定義と出力設定のデータセット。
 * このオブジェクトは、Excel ファイルの変換定義における、一つ分の CSV 変換出力設定を保持する。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcConfigCsvData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 変換定義のトップレベルコマンド **/
	private final CmdCsvFileNode	_cmdnode;

	/** 出力先ファイル **/
	private File	_destFile;
	/** 変換タイトル、未定義の場合は <tt>null</tt> **/
	private String	_strTitle;
	/** 標準ファイル名(拡張子除く)、未定義の場合は <tt>null</tt> **/
	private String	_strDefFilename;
	/** テンポラリファイルのプレフィックス **/
	private String	_strTempFilePrefix;
	/** テンポラリファイルへの出力とする場合は <tt>true</tt> **/
	private boolean	_flgOutTempFile;
	/** 変換結果ファイルを表示する場合は <tt>true</tt> **/
	private boolean	_flgShowDestFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EtcConfigCsvData(final CmdCsvFileNode cmdnode) {
		String strValue;
		//--- 変換タイトル
		strValue = cmdnode.getTitleParameter().getValue().toString();
		_strTitle = (strValue.isEmpty() ? null : strValue);
		//--- 標準ファイル名
		strValue = cmdnode.getFilenameParameter().getValue().toString();
		_strDefFilename = (strValue.isEmpty() ? null : strValue);
		_strTempFilePrefix = _strDefFilename;
		//--- 初期化
		_cmdnode = cmdnode;
		_destFile = null;
		_flgOutTempFile  = false;
		_flgShowDestFile = false;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CmdCsvFileNode getCommand() {
		return _cmdnode;
	}

	/**
	 * 変換タイトルを取得する。
	 * @return	設定されている変換タイトル、未定義の場合は <tt>null</tt>
	 */
	public String getConversionTitle() {
		return _strTitle;
	}

	/**
	 * 変換タイトルを設定する。
	 * @param title	設定する文字列、<tt>null</tt> もしくは空文字の場合は未定義となる
	 */
	public void setConversionTitle(String title) {
		_strTitle = (Strings.isNullOrEmpty(title) ? null : title);
	}

	/**
	 * 標準ファイル名(拡張子は含まれない)を取得する。
	 * @return	設定されている標準ファイル名、未定義の場合は <tt>null</tt>
	 */
	public String getDefaultFilename() {
		return _strDefFilename;
	}

	/**
	 * 拡張子を含まない標準ファイル名を設定する。
	 * @param name	設定する文字列、<tt>null</tt> もしくは空文字の場合は未定義となる
	 */
	public void setDefaultFilename(String name) {
		_strDefFilename = (Strings.isNullOrEmpty(name) ? null : name);
	}

	/**
	 * 設定されている出力先ファイルを取得する。
	 * @return	出力先ファイルの抽象パス、未定義の場合は <tt>null</tt>
	 */
	public File getDestFile() {
		return _destFile;
	}

	/**
	 * 出力先ファイルを設定する。
	 * @param file	設定する抽象パス、<tt>null</tt> の場合は未定義となる
	 */
	public void setDestFile(File file) {
		_destFile = file;
	}

	/**
	 * テンポラリファイル用プレフィックスを取得する。
	 * @return	設定されている文字列、未定義の場合は <tt>null</tt>
	 */
	public String getTemporaryPrefix() {
		return _strTempFilePrefix;
	}

	/**
	 * テンポラリファイル用プレフィックスを設定する。
	 * @param prefix	設定する文字列、<tt>null</tt> もしくは空文字の場合は未定義となる
	 */
	public void setTemporaryPrefix(String prefix) {
		_strTempFilePrefix = (Strings.isNullOrEmpty(prefix) ? null : prefix);
	}

	/**
	 * テンポラリファイルに出力するかどうかの設定を取得する。
	 * @return	テンポラリファイルに出力される場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean getOutputTemporaryEnabled() {
		return _flgOutTempFile;
	}

	/**
	 * テンポラリファイルに出力するかどうかを設定する。
	 * @param toEnable	テンポラリファイルに出力する場合は <tt>true</tt>、そうでないなら <tt>false</tt>
	 */
	public void setOutputTemporaryEnabled(boolean toEnable) {
		_flgOutTempFile = toEnable;
	}

	/**
	 * 変換結果ファイルの内容を表示するかどうかの設定を取得する。
	 * @return	変換結果が表示される場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean getShowDestFileEnabled() {
		return _flgShowDestFile;
	}

	/**
	 * 変換結果ファイルの内容を表示するかどうかを設定する。
	 * @param toEnable	変換結果を表示する場合は <tt>true</tt>、そうでないなら <tt>false</tt>
	 */
	public void setShowDestFileEnabled(boolean toEnable) {
		_flgShowDestFile = toEnable;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
