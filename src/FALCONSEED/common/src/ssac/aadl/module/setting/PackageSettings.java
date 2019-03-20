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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)PackageSettings.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.IOException;

import ssac.util.Strings;
import ssac.util.io.VirtualFile;

/**
 * モジュールパッケージ設定情報を保持するクラス。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class PackageSettings extends AbstractSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String SECTION = "package";
	static public final String KEY_TITLE = SECTION + ".title";
	static public final String KEY_DESC  = SECTION + ".description";
	static public final String KEY_MAIN  = SECTION + ".main";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** メインモジュールファイルへの絶対パス **/
	private VirtualFile	_mainModule;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PackageSettings() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//
	// Title
	//

	/**
	 * モジュールパッケージのタイトルが設定されている場合に <tt>true</tt> を返す。
	 * 有効な文字列がタイトルとして設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedTitle() {
		return (getTitle() != null);
	}

	/**
	 * モジュールパッケージのタイトルを返す。
	 * 有効な文字列がタイトルとして設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getTitle() {
		String t = props.getString(KEY_TITLE, null);
		return (Strings.isNullOrEmpty(t) ? null : t);
	}

	/**
	 * モジュールパッケージのタイトルを設定する。
	 * @param title	タイトルを表す文字列を指定する。有効な文字列ではない場合は、
	 * 				エントリを除去する。
	 */
	public void setTitle(String title) {
		if (Strings.isNullOrEmpty(title)) {
			props.clearProperty(KEY_TITLE);
		} else {
			props.setString(KEY_TITLE, title);
		}
	}
	
	//
	// Description
	//

	/**
	 * モジュールパッケージの説明が設定されている場合に <tt>true</tt> を返す。
	 * 有効な文字列が説明として設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedDescription() {
		return (getDescription() != null);
	}

	/**
	 * モジュールパッケージの説明を返す。
	 * 有効な文字列が説明として設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getDescription() {
		String d = props.getString(KEY_DESC, null);
		return (Strings.isNullOrEmpty(d) ? null : d);
	}

	/**
	 * モジュールパッケージの説明を設定する。
	 * @param desc	説明を表す文字列を指定する。有効な文字列ではない場合は、
	 * 				エントリを除去する。
	 */
	public void setDescription(String desc) {
		if (Strings.isNullOrEmpty(desc)) {
			props.clearProperty(KEY_DESC);
		} else {
			props.setString(KEY_DESC, desc);
		}
	}
	
	//
	// Main module
	//

	/**
	 * モジュールパッケージのメインモジュールが設定されている場合に <tt>true</tt> を返す。
	 * 設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedMainModule() {
		return (_mainModule != null);
	}

	/**
	 * モジュールパッケージのメインモジュールの絶対パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMainModuleFile() {
		return _mainModule;
	}

	/**
	 * モジュールパッケージのメインモジュールを設定する。
	 * @param path	メインモジュールとするファイルの抽象パス
	 */
	public void setMainModuleFile(VirtualFile path) {
		_mainModule = (path==null ? null : path.getAbsoluteFile());
	}

	/**
	 * モジュールパッケージのメインモジュールの相対パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getRelativeMainModuleFile() {
		return props.getString(KEY_MAIN, null);
	}

	//------------------------------------------------------------
	// Implement AbstractSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// ファイルパスを相対パスに変換し、プロパティに保存
		setAbsoluteVirtualFileProperty(KEY_MAIN, _mainModule);
		
		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// プロパティからファイルパスを取得する
		//--- プロパティのファイルパスは基本的に相対パスで保存されている
		//--- 相対パスの場合は、このプロパティファイルからの相対とする
		_mainModule = getAbsoluteVirtualFileProperty(KEY_MAIN, null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
