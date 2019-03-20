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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)VirtualFilePathFormatter.java	1.20	2012/03/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import ssac.util.io.VirtualFile;

/**
 * パスの表示名整形インタフェースの標準実装。
 * このクラスでは、単一のベースパスと、そのベースパスを置き換える表示名を指定する。
 * ベースパスの相対とならない場合は、絶対パスで表示される。なお、絶対パスの場合は、
 * ファイル区切り文字は変更しない。
 * 
 * @version 1.20	2012/03/20
 * @since 1.20
 */
public class DefaultVirtualFilePathFormatter extends AbVirtualFilePathFormatter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected boolean		_rootVisible;
	protected VirtualFile	_baseFile;
	protected String		_dispName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DefaultVirtualFilePathFormatter() {
		this._rootVisible = true;
		this._baseFile = null;
		this._dispName = null;
	}

	/**
	 * 指定された基準パスによってパスを整形するパスフォーマッターを生成する。
	 * このメソッドでは整形パスの先頭にルートを示すパス文字列が付加され、
	 * 基準パスのパスを含まないファイル名が先頭に配置される。
	 * @param baseFile	基準となる抽象パス
	 */
	public DefaultVirtualFilePathFormatter(VirtualFile baseFile) {
		this(baseFile, null, true);
	}

	/**
	 * 指定されたパラメータで、新しいパスフォーマッターを生成する。
	 * このコンストラクタで生成した場合、整形されたパスの先頭にはルートを示すパス文字列が付加される。
	 * @param baseFile	基準となる抽象パス
	 * @param dispName	基準となる抽象パスを置き換える表示名を指定する。
	 * 					<tt>null</tt> を指定した場合、基準パスのファイル名が表示名となる。
	 * 					また、空文字列を指定した場合、基準パスを含まないパス文字列を整形する。
	 */
	public DefaultVirtualFilePathFormatter(VirtualFile baseFile, String dispName) {
		this(baseFile, dispName, true);
	}

	/**
	 * 指定されたパラメータで、新しいパスフォーマッターを生成する。
	 * @param baseFile	基準となる抽象パス
	 * @param dispName	基準となる抽象パスを置き換える表示名を指定する。
	 * 					<tt>null</tt> を指定した場合、基準パスのファイル名が表示名となる。
	 * 					また、空文字列を指定した場合、基準パスを含まないパス文字列を整形する。
	 * @param rootSeparatorVisible	整形されたパスの先頭にルートを示すパス区切り文字を付加する場合は <tt>true</tt>
	 */
	public DefaultVirtualFilePathFormatter(VirtualFile baseFile, String dispName, boolean rootSeparatorVisible) {
		this._rootVisible = rootSeparatorVisible;
		this._baseFile = baseFile;
		this._dispName = dispName;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 整形後のパス文字列の先頭にファイル区切り文字を表示する場合に <tt>true</tt> を返す。
	 * このメソッドが <tt>true</tt> を返す場合、整形後のパス文字列の先頭には、
	 * ファイル区切り文字が付加される。
	 */
	public boolean isRootSeparatorVisible() {
		return _rootVisible;
	}

	/**
	 * 整形後のパス文字列の先頭にファイル区切り文字を表示するかどうかを設定する。
	 * @param toVisible	整形後パス文字列の先頭にファイル区切り文字を表示する場合は <tt>true</tt>
	 */
	public void setRootSeparatorVisible(boolean toVisible) {
		this._rootVisible = toVisible;
	}

	/**
	 * 基準パスの表示名を返す。
	 * このメソッドが <tt>null</tt> を返す場合、整形後の表示名は基準パスのファイル名となる。
	 * @return	基準パスの表示名
	 */
	public String getDisplayName() {
		return _dispName;
	}

	/**
	 * 基準パスの表示名を設定する。
	 * @param name	基準パスの表示名とする文字列。
	 */
	public void setDisplayName(String name) {
		this._dispName = name;
	}

	/**
	 * このオブジェクトの基準パスを返す。
	 * @return	基準パスの抽象パス
	 */
	public VirtualFile getBaseFile() {
		return _baseFile;
	}

	/**
	 * このオブジェクトの基準パスを設定する。
	 * <tt>file</tt> に <tt>null</tt> を指定した場合、整形を行うメソッド呼び出しは失敗する場合がある。
	 * @param file	基準パスの抽象パス
	 */
	public void setBaseFile(VirtualFile file) {
		this._baseFile = file;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
